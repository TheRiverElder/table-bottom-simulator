package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.PlaceholderBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.SELLABLE_FACTORY_TYPE_LIST
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionBase
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOption
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOptions
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.city
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.factory
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject

class SellAction(val birminghamGamer: BirminghamGamer, costCardObjectUid: Int) :
    ActionBase(birminghamGamer.user!!, costCardObjectUid) {

    val cityObjectUidList = ArrayList<Pair<Int, Int>>() // city.uid to factory.uid
    val tavernObjectUidList = ArrayList<Int>()
    var done: Boolean = false

    override val options: ActionOptions?
        get() {
            if (done) return null

            val cityObjectUidList = cityObjectUidList
            val tavernObjectUidList = tavernObjectUidList
            val game = birminghamGamer.game

            if (cityObjectUidList.size <= tavernObjectUidList.size) {
                val options =
                    game.simulator.gameObjects.values.mapNotNull<GameObject, Pair<GameObject, GameObject>> { cityGameObject ->
                        if (!cityGameObject.tags.containsKey("birmingham:city")) return@mapNotNull null
                        if (cityObjectUidList.any { it.first == cityGameObject.uid || it.second == cityGameObject.uid }) return@mapNotNull null

                        val placeholderBehavior =
                            cityGameObject.getBehaviorByType(PlaceholderBehavior.TYPE) ?: return@mapNotNull null

                        var factoryGameObject: GameObject? = null
                        for (gameObject in placeholderBehavior.holdingGameObjects) {
                            if (!gameObject.tags.containsKey("birmingham:factory")) continue
                            val cardBehavior = gameObject.getBehaviorByType(CardBehavior.TYPE) ?: continue
                            if (cardBehavior.flipped) continue
                            val factory = gameObject.factory
                            if (factory.ownerGamerUid != birminghamGamer.gamerUid || factory.typeName !in SELLABLE_FACTORY_TYPE_LIST) continue
                            factoryGameObject = gameObject
                        }
                        if (factoryGameObject == null) null else (cityGameObject to factoryGameObject)
                    }.map { pair ->
                        val cityGameObject: GameObject = pair.first
                        val city = cityGameObject.city
                        val factoryGameObject = pair.second
                        ActionOption("${city.name} 第${city.index + 1}个槽") {
                            this.cityObjectUidList.add(cityGameObject.uid to factoryGameObject.uid)
                        }
                    }
                return ActionOptions("选择一个带有待卖出工厂的城市（第${cityObjectUidList.size + 1}个）", options)
            } else if (cityObjectUidList.size > 0) {
                // TODO
                val pair = cityObjectUidList.lastOrNull() ?: return null
                val cityGameObject = game.simulator.gameObjects[pair.first]!!
                val factoryGameObject = game.simulator.gameObjects[pair.second]!!
                val factory = factoryGameObject.factory

                val options =
                    game.simulator.gameObjects.values.mapNotNull { gameObject ->
                        if (!gameObject.tags.containsKey("birmingham:tavern")) return@mapNotNull null
                        if (!gameObject.tags.containsKey("birmingham:city")) return@mapNotNull null
                        val city = gameObject.city
                        if (factory.typeName !in city.factoryTypeNames) return@mapNotNull null
                        gameObject
                    }.map { gameObject ->
                        val city = gameObject.city
                        ActionOption("卖到 ${city.name} 第${city.index + 1}个槽") {
                            this.tavernObjectUidList.add(gameObject.uid)
                        }
                    }
                return ActionOptions("选择一个可以谈${factory.typeName}生意的酒馆（第${tavernObjectUidList.size + 1}个）", options)
            } else return null
        }

    override fun reset() {
        cityObjectUidList.clear()
        tavernObjectUidList.clear()
        done = false
    }

    override val fulfilled: Boolean get() = done && tavernObjectUidList.isNotEmpty()

    override fun perform() {
        val game = birminghamGamer.game
        // TODO 花费对应的啤酒资源

        for (pair in cityObjectUidList) {
            val factoryGameObject = game.simulator.gameObjects[pair.second]!!
            val cardBehavior = factoryGameObject.getBehaviorByType(CardBehavior.TYPE)!!
            cardBehavior.flipped = true
            cardBehavior.sendUpdate()

            val factory = factoryGameObject.factory
            // TODO 卖出的奖励

        }

        game.extension.channel.sendUpdateGameState()
    }
}