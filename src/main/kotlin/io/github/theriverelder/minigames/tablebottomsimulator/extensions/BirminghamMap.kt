package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.*
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class BirminghamMap(
    val extension: BirminghamExtension,
) : Persistable {

    val cities = Registry<Int, City> { it.placeholderObjectUid }
    val networks = Registry<Int, Network> { it.placeholderObjectUid }
    val markets = Registry<Int, Market> { it.placeholderObjectUid }

    // 不序列化
    val cityGroupList = mutableListOf<Group<City, Network>>()
    val marketGroupList = mutableListOf<Group<Market, Network>>()

    override fun save(): JsonObject = buildJsonObject {
        put("cities", cities.save())
        put("networkList", networks.save())
    }

    override fun restore(data: JsonObject) {
        cities.clear()
        cities += data.forceGet("cities").jsonArray.map { restoreCity(it.jsonObject) }
        networks.clear()
        networks += data.forceGet("networks").jsonArray.map { restoreNetwork(it.jsonObject) }
        buildGraph()
        cacheHoldingObjects()
    }

    fun organize() {
        val cityGameObjects = arrayListOf<Pair<GameObject, City>>()
        val networkGameObjects = arrayListOf<Pair<GameObject, Network>>()

        cities.clear()
        networks.clear()
        markets.clear()

        for (gameObject in extension.simulator.gameObjects.values) {
            val city = gameObject.city
            if (city != null) {
                cityGameObjects.add(gameObject to city)
                cities.add(city)
                continue
            }
            val network = gameObject.network
            if (network != null) {
                networkGameObjects.add(gameObject to network)
                networks.add(network)
                continue
            }
            val market = gameObject.market
            if (market != null) {
                markets.add(market)
                continue
            }
        }

        buildGraph()
        cacheHoldingObjects()
    }

    fun buildGraph() {

        cityGroupList.clear()
        cityGroupList.addAll(cities.values.groupBy { it.name }
            .map { pair -> Group<City, Network>(pair.key).also { it.content = pair.value.toList() } })
        cityGroupList.forEach { group -> group.content.forEach { it.group = group } }

        marketGroupList.clear()
        marketGroupList.addAll(markets.values.groupBy { it.name }
            .map { pair -> Group<Market, Network>(pair.key).also { it.content = pair.value.toList() } })
        marketGroupList.forEach { group -> group.content.forEach { it.group = group } }

        val cityGroupNameMap = buildMap { cityGroupList.forEach { set(it.name, it) } }


        for (network in networks.values) {
            val cityGroups = network.cityNames.mapNotNull { cityGroupNameMap[it] }
            network.neighbors = cityGroups
            cityGroups.forEach { it.neighbors += network }
        }
    }

    fun cacheHoldingObjects() {
        for (city in cities.values) {
            city.cachedFactory = getHoldingFactory(extension, city.placeholderObjectUid)
        }
        for (network in networks.values) {
            network.cachedWay = getHoldingWay(extension, network.placeholderObjectUid)
        }
        for (market in markets.values) {
            market.cachedTavern = getHoldingTavern(extension, market.placeholderObjectUid)
        }
    }

    // 检查是否能把工厂建在某个城市
    // 只检查网络，不检查手牌，不检查所需的资源
    fun canBuildAt(city: City, factory: Factory, gamer: BirminghamGamer): Boolean {
        // 如果这个位置已经有工厂，则不能建造
        if (getHoldingFactory(extension, city.placeholderObjectUid) != null) return false
        // 如果产业类型对应不上，也不能建造
        if (factory.typeName !in city.factoryTypeNames) return false
        // 如果该地区已经有该玩家的建筑：运河时代，不能建造；铁路时代：可以建造
        if (city.group.content.any { it.cachedFactory?.ownerGamerUid == gamer.gamerUid }) {
            return when (extension.birminghamGame?.period) {
                1 -> false
                2 -> true
                else -> false
            }
        }
        // 如果场上没有该玩家的建筑，则可以建造
        if (cities.values.none { it.cachedFactory?.ownerGamerUid == gamer.gamerUid }) return true
        // 如果连接到该玩家的网络，可以建造
        if (city.group.neighbors.any { it.cachedWay?.ownerGameUid == gamer.gamerUid }) return true
        // 其它情况不能建造
        return false
    }

    fun checkConnected(
        head: GraphNode<GraphNode<*, BirminghamGraphNodeContext>, BirminghamGraphNodeContext>,
        tail: GraphNode<GraphNode<*, BirminghamGraphNodeContext>, BirminghamGraphNodeContext>,
        gamer: BirminghamGamer
    ): Boolean {
        val context = BirminghamGraphNodeContext(extension.birminghamGame ?: return false, gamer)

        // TODO
        return true
    }

}