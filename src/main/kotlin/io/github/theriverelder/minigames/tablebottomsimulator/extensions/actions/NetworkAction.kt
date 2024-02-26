package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel.UpdateGameObjectSelfOptions
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionBase
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOption
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOptions

class NetworkAction(val birminghamGamer: BirminghamGamer, costCardObjectUid: Int) :
    ActionBase(birminghamGamer.user!!, costCardObjectUid) {

    var networkAmountInThisAction: Int? = null
    val networkObjectUidList = ArrayList<Int>()

    override val options: ActionOptions?
        get() {

            val networkAmount = networkAmountInThisAction
            val networkUidList = networkObjectUidList
            val game = birminghamGamer.game

            if (networkAmount == null) {
                val maxNetworkAmountInThisAction = when (game.period) {
                    1 -> 1
                    2 -> 2
                    else -> 0
                }
                val wayTypeName = when (game.period) {
                    1 -> "运河"
                    2 -> "铁道"
                    else -> "道路"
                }

                return ActionOptions("选择放置 $wayTypeName 的数量：", buildList(maxNetworkAmountInThisAction) {
                    repeat(maxNetworkAmountInThisAction) { index ->
                        val amount = index + 1
                        add(ActionOption("$amount 条") {
                            this@NetworkAction.networkAmountInThisAction = amount
                        })
                    }
                })
            } else if (networkUidList.size < networkAmount) {
                val options = game.extension.birminghamMap.networks.values
                    .filter { it.cachedWay == null }
                    .map {
                        val uid = it.placeholderObjectUid
                        ActionOption("在 ${it.cityNames.joinToString(" 与 ")} 之间") {
                            networkObjectUidList.add(uid)
                        }
                    }
                return ActionOptions("选择放置道路的位置（${networkUidList.size + 1}/${networkAmount}）：", options)
            } else return null
        }

    override fun reset() {
        networkAmountInThisAction = null
        networkObjectUidList.clear()
    }

    override val fulfilled: Boolean
        get() {
            return networkObjectUidList.size >= (networkAmountInThisAction ?: return false)
        }

    override fun perform() {
        val game = birminghamGamer.game
        // TODO 花费对应的啤酒煤炭金钱资源

        for (networkObjectUid in networkObjectUidList) {
            val networkGameObject = game.simulator.gameObjects[networkObjectUid]!!
            val wayObject = birminghamGamer.createWayObject()

            wayObject.position = networkGameObject.position
            wayObject.rotation = networkGameObject.rotation

            wayObject.sendUpdateSelf(UpdateGameObjectSelfOptions(position = true, rotation = true))

        }

        game.extension.channel.sendUpdateGameState()
    }
}