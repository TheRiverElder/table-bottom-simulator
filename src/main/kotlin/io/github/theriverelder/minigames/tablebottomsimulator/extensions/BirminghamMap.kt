package io.github.theriverelder.minigames.tablebottomsimulator.extensions

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

    val cityList = mutableListOf<City>()
    val networkList = mutableListOf<Network>()

    override fun save(): JsonObject = buildJsonObject {
        put("cityList", cityList.save())
        put("networkList", networkList.save()) }

    override fun restore(data: JsonObject) {
        cityList.clear()
        cityList += data.forceGet("cityList").jsonArray.map { restoreCity(it.jsonObject) }
        networkList.clear()
        networkList += data.forceGet("networkList").jsonArray.map { restoreNetwork(it.jsonObject) }
    }

    fun organize() {
        val cityGameObjects = arrayListOf<Pair<GameObject, City>>()
        val networkGameObjects = arrayListOf<Pair<GameObject, Network>>()

        cityList.clear()
        networkList.clear()

        for (gameObject in extension.simulator.gameObjects.values) {
            val citySlotLocationTag = gameObject.tags["birmingham:city"]
            if (citySlotLocationTag != null) {
                val city = gameObject.city
                cityGameObjects.add(gameObject to city)
                cityList.add(city)
                continue
            }
            val networkTag = gameObject.tags["birmingham:network"]
            if (networkTag != null) {
                val network = gameObject.network
                networkGameObjects.add(gameObject to network)
                networkList.add(network)
                continue
            }
        }

    }
}