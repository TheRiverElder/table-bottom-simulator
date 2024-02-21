package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.PlaceholderBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel.UpdateGameObjectSelfOptions
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionBase
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOption
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOptions
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.cityName
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.factoryTypeNames
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.City
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.factory

class BuildAction(val birminghamGamer: BirminghamGamer, costCardObjectUid: Int) :
    ActionBase(birminghamGamer.user!!, costCardObjectUid) {

    var factoryObjectUid: Int? = null
    var cityObjectUid: Int? = null

    override val options: ActionOptions?
        get() {
            val costCard = costCard
            val game = birminghamGamer.game
            val extension = birminghamGamer.game.extension
            val factoryObjectUid = factoryObjectUid
            val cityObjectUid = cityObjectUid

            return if (factoryObjectUid == null) {
                var cardFactoryTypeNames = costCard.factoryTypeNames

                if (cardFactoryTypeNames == null) {
                    val cityName = costCard.cityName
                        ?: return ActionOptions("不是合法的工厂牌或城市牌：", listOf(
                            ActionOption("重新选牌") { birminghamGamer.actionGuide?.reset() }
                        ))
                    val cities = extension.birminghamMap.cityList
                        .filter { it.name == cityName || cityName == "" }
                        .filter {
                            val gameObject = game.simulator.gameObjects[it.placeholderObjectUid] ?: return@filter false
                            val placeholderBehavior =
                                gameObject.getBehaviorByType(PlaceholderBehavior.TYPE) ?: return@filter false
                            placeholderBehavior.holdingGameObjects.isEmpty()
                        }
                    cardFactoryTypeNames = if (cities.isEmpty()) null else
                        cities.flatMap { it.factoryTypeNames }.toSet().toList()
                }

                if (cardFactoryTypeNames == null) return ActionOptions("无可用产业类型：", emptyList())

                val validFactories = birminghamGamer.factoryObjectUidStacks
                    .map { it.key to it.value.firstOrNull() }
                    .filter { it.second != null }
                    .filter { cardFactoryTypeNames.isEmpty() || it.first in cardFactoryTypeNames }

                ActionOptions("选择产业类型：", validFactories.map {
                    ActionOption(it.first) { this.factoryObjectUid = it.second }
                })

            } else if (cityObjectUid == null) {
                val cityName = costCard.cityName
                val cities: List<City> = if (cityName != null)
                    extension.birminghamMap.cityList
                        .filter { it.name == cityName }
                        .filter {
                            val gameObject = game.simulator.gameObjects[it.placeholderObjectUid] ?: return@filter false
                            val placeholderBehavior =
                                gameObject.getBehaviorByType(PlaceholderBehavior.TYPE) ?: return@filter false
                            placeholderBehavior.holdingGameObjects.isEmpty()
                        }
                else {
                    val factory = birminghamGamer.gamer!!.simulator.gameObjects[factoryObjectUid]!!.factory
                    extension.birminghamMap.cityList.filter { it.factoryTypeNames.contains(factory.typeName) }
                }

                ActionOptions("选择城市：", cities.map {
                    val uid = it.placeholderObjectUid
                    val city = it
                    ActionOption("${city.name}的第${city.index + 1}个槽") { this.cityObjectUid = uid }
                })
            } else null
        }

    override fun reset() {
        factoryObjectUid = null
        cityObjectUid = null
    }

    override val fulfilled: Boolean
        get() = factoryObjectUid != null && cityObjectUid != null

    override fun perform() {
        val factoryObjectUid = factoryObjectUid ?: return
        val factoryObject = birminghamGamer.gamer!!.simulator.gameObjects[factoryObjectUid]!!
        val cityObject = birminghamGamer.gamer!!.simulator.gameObjects[cityObjectUid!!]!!

        val factory = factoryObject.factory

        factoryObject.position = cityObject.position
        val newList = birminghamGamer.factoryObjectUidStacks[factory.typeName]!!.toMutableList()
        newList -= factoryObjectUid
        birminghamGamer.factoryObjectUidStacks[factory.typeName] = newList

        factoryObject.sendUpdateSelf(UpdateGameObjectSelfOptions(position = true))
    }
}