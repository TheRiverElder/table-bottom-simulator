package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.PlaceholderBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionBase
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOption
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOptions
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.city
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.factory
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.factoryTypeNames

class BuildAction(val birminghamGamer: BirminghamGamer, costCard: Card) : ActionBase(birminghamGamer.user!!, costCard) {

    var factoryObjectUid: Int? = null
    var cityObjectUid: Int? = null

    override val options: ActionOptions?
        get() {
            val factoryObjectUid = factoryObjectUid
            val cityObjectUid = cityObjectUid

            return if (factoryObjectUid == null) {
                val cardFactoryTypeNames = costCard.factoryTypeNames

                if (cardFactoryTypeNames == null) {
                    ActionOptions("不是合法的产业牌：${costCard.name}", listOf(
                        ActionOption("重新选牌") { birminghamGamer.actionGuide?.reset() }
                    ))
                } else {
                    val validFactories = birminghamGamer.factoryObjectUidStacks
                        .map { it.key to it.value.firstOrNull() }
                        .filter { it.second != null }
                        .filter { cardFactoryTypeNames.isEmpty() || it.first in cardFactoryTypeNames }

                    ActionOptions("选择产业类型：", validFactories.map {
                        val uid = it.second
                        ActionOption(it.first) { this.factoryObjectUid = it.second }
                    })
                }
            } else if (cityObjectUid == null) {
                val factory = birminghamGamer.gamer!!.simulator.gameObjects[factoryObjectUid]!!.factory

                val cities = birminghamGamer.game.simulator.gameObjects.values
                    .filter { it.tags.containsKey("birmingham:city_name") }
                    .filterNot { obj ->
                        obj.getBehaviorByType(PlaceholderBehavior.TYPE)?.holdingGameObjects
                            ?.any { it.tags.containsKey("birmingham:factory") }
                            ?: false
                    }.map { it to it.city }
                    .filter { pair -> pair.second.factoryTypes.contains(factory.typeName) }

                ActionOptions("选择城市：", cities.map {
                    val uid = it.first.uid
                    val city = it.second
                    ActionOption(city.name) { this.cityObjectUid = uid }
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
        val factoryObject = birminghamGamer.gamer!!.simulator.gameObjects[factoryObjectUid!!]!!
        val cityObject = birminghamGamer.gamer!!.simulator.gameObjects[cityObjectUid!!]!!

        factoryObject.position = cityObject.position
        factoryObject.sendUpdateSelf()
    }
}