package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.SELLABLE_FACTORY_TYPE_LIST
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionBase
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOption
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOptions

class SellAction(val birminghamGamer: BirminghamGamer, costCardObjectUid: Int) :
    ActionBase(birminghamGamer.user!!, costCardObjectUid) {

    val cityObjectUidList = ArrayList<Int>() // city.uid
    val marketObjectUidList = ArrayList<Int>()
    var done: Boolean = false

    override val options: ActionOptions?
        get() {
            if (done) return null

            val cityObjectUidList = cityObjectUidList
            val marketObjectUidList = marketObjectUidList
            val game = birminghamGamer.game

            if (cityObjectUidList.size <= marketObjectUidList.size) {
                val options =
                    game.extension.birminghamMap.cities.values.mapNotNull { city ->
                        if (cityObjectUidList.any { it == city.placeholderObjectUid }) return@mapNotNull null

                        val factory = city.cachedFactory ?: return@mapNotNull null
                        if (factory.ownerGamerUid != birminghamGamer.gamerUid) return@mapNotNull null
                        if (factory.typeName !in SELLABLE_FACTORY_TYPE_LIST) return@mapNotNull null

                        ActionOption("${city.name}的第${city.index + 1}个槽（${factory.typeName}）") {
                            this.cityObjectUidList.add(city.placeholderObjectUid)
                        }
                    }
                return ActionOptions("选择一个带有待卖出工厂的城市（第${cityObjectUidList.size + 1}个）", options)
            } else {
                // TODO
                val city =
                    game.extension.birminghamMap.cities[cityObjectUidList.lastOrNull() ?: return null] ?: return null
                val factory = city.cachedFactory ?: return null

                val options =
                    game.extension.birminghamMap.markets.values.mapNotNull { market ->
                        val tavern = market.cachedTavern ?: return@mapNotNull null
                        ActionOption("卖到 ${city.name} 第${city.index + 1}个槽（${tavern.factoryTypeNames.joinToString()}）") {
                            this.marketObjectUidList.add(city.placeholderObjectUid)
                        }
                    }
                return ActionOptions(
                    "选择一个可以谈${factory.typeName}生意的酒馆（第${marketObjectUidList.size + 1}个）",
                    options
                )
            }
        }

    override fun reset() {
        cityObjectUidList.clear()
        marketObjectUidList.clear()
        done = false
    }

    override val fulfilled: Boolean get() = done && marketObjectUidList.isNotEmpty()

    override fun perform() {
        val game = birminghamGamer.game
        // TODO 花费对应的啤酒资源

        for (cityUid in cityObjectUidList) {
            val city = game.extension.birminghamMap.cities[cityUid] ?: return
            val factory = city.cachedFactory ?: return
            val factoryObject = game.simulator.gameObjects[factory.gameObjectUid] ?: return
            val cardBehavior = factoryObject.getBehaviorByType(CardBehavior.TYPE)!!
            cardBehavior.flipped = true
            cardBehavior.sendUpdate()

            // TODO 卖出的奖励

        }

        game.extension.channel.sendUpdateGameState()
    }
}