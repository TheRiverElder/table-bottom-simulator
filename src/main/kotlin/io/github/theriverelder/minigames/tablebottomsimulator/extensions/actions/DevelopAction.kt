package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionBase
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOption
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOptions
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.Factory
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.factory

class DevelopAction(val birminghamGamer: BirminghamGamer, costCardObjectUid: Int) :
    ActionBase(birminghamGamer.user!!, costCardObjectUid) {

    val selectedFactoryObjectUidList = ArrayList<Int>(2)
    var done: Boolean = false

    override val options: ActionOptions?
        get() {
            if (selectedFactoryObjectUidList.size >= 2) return null

            val uidList = selectedFactoryObjectUidList
            val game = birminghamGamer.game
            val options = birminghamGamer.factoryObjectUidStacks
                .mapNotNull { stack -> stack.value.firstOrNull { it !in uidList } }
                .filter { it !in uidList }
                .mapNotNull {
                    val factory: Factory = game.simulator.gameObjects[it]?.factory ?: return@mapNotNull null
                    ActionOption("${factory.typeName} level ${factory.level + 1}") { selectedFactoryObjectUidList.add(it) }
                }.let {
                    if (uidList.isEmpty()) it
                    else it + ActionOption("研发掉${uidList.size}个就够了") { done = true }
                }
            return ActionOptions("请选择一个工厂指示物丢弃（${uidList.size + 1}/2）", options)
        }

    override fun reset() {
        selectedFactoryObjectUidList.clear()
        done = false
    }

    override val fulfilled: Boolean get() = done || selectedFactoryObjectUidList.size >= 2

    override fun perform() {
        val game = birminghamGamer.game
        // TODO 花费对应的钢铁资源

        selectedFactoryObjectUidList
            .mapNotNull { game.simulator.gameObjects[it] }
            .forEach { game.discardGameObject(it) }

        for (uid in selectedFactoryObjectUidList) {
            var key: String? = null
            for (factoryObjectUidStack in birminghamGamer.factoryObjectUidStacks) {
                val uidList = factoryObjectUidStack.value
                if ( uidList.isEmpty() || uidList.first() != uid) continue

                key = factoryObjectUidStack.key
                break
            }
            if (key != null) {
                val list = birminghamGamer.factoryObjectUidStacks[key]!!
                birminghamGamer.factoryObjectUidStacks[key] = list.drop(1)
            }
        }

        game.extension.channel.sendUpdateGameState()
    }
}