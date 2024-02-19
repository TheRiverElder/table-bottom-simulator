package io.github.theriverelder.minigames.tablebottomsimulator.extensions.action

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.BuildAction
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.LoanAction
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.ScoutAction

class ActionGuide(val birminghamGamer: BirminghamGamer) {

    var costCardObjectUid: Int? = null
    var action: Action? = null

    var options: ActionOptions? = null

    private val actionCreators = listOf(
        ActionCreator("build", ::BuildAction),
        ActionCreator("sell(loan)") { user, costCardObjectUid -> LoanAction(user, costCardObjectUid) },
        ActionCreator("loan", ::LoanAction),
        ActionCreator("scout", ::ScoutAction),
        ActionCreator("network(loan)") { user, costCardObjectUid -> LoanAction(user, costCardObjectUid) },
        ActionCreator("develop(loan)") { user, costCardObjectUid -> LoanAction(user, costCardObjectUid) },
    )

    fun update() {
        val costCardObjectUid = costCardObjectUid
        val simulator = birminghamGamer.game.simulator
        val costCard =
            if (costCardObjectUid != null) simulator.gameObjects[costCardObjectUid]!!.getBehaviorByType(CardBehavior.TYPE)!!.card else null
        val action = action
        if (costCardObjectUid == null || costCard == null) {
            options = birminghamGamer.gamer?.cardObjects?.let {
                ActionOptions(
                    "选择一张手牌：",
                    it.map { cardObject ->
                        val card = cardObject.getBehaviorByType(CardBehavior.TYPE)!!.card!!
                        ActionOption(card.name) { this.costCardObjectUid = cardObject.uid }
                    }
                )
            }
        } else if (action == null) {
            options = ActionOptions(
                "选择以下行动之一：",
                actionCreators.map { creator ->
                    ActionOption(creator.name) {
                        this.action = creator.create(birminghamGamer, costCardObjectUid)
                    }
                }
            )
        } else if (!action.fulfilled) {
            options = action.options
        } else {
            options = ActionOptions(
                "确认行动",
                listOf(ActionOption("确认") {
                    action.perform()
                    birminghamGamer.game.step()
                })
            )
        }

    }

    fun choose(index: Int) {
        val action = action
        val costCardObjectUid = costCardObjectUid
        if (action != null && costCardObjectUid != null && action.fulfilled) {
            // 消耗手牌
            birminghamGamer.gamer?.removeCardFromHand(costCardObjectUid)
            birminghamGamer.game.simulator.gameObjects[costCardObjectUid]?.let { birminghamGamer.game.discardCard(it) }
            // 最终执行行动
            action.perform()

            resetDirectly()
            update()

            return
        } else {
            val option = options?.options?.getOrNull(index)
            val function = option?.callback
            if (function != null) {
                function()
                update()
            }
        }

        birminghamGamer.game.listenerGameStateUpdated.emit(birminghamGamer.game)
        birminghamGamer.game.listenerActionOptionsUpdated.emit(birminghamGamer)
    }

    fun resetDirectly() {
        costCardObjectUid = null
        action = null
        options = null
    }

    fun reset() {
        resetDirectly()

        birminghamGamer.game.listenerGameStateUpdated.emit(birminghamGamer.game)
        birminghamGamer.game.listenerActionOptionsUpdated.emit(birminghamGamer)
    }
}