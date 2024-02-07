package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer

class ActionGuide(val birminghamGamer: BirminghamGamer) {

    var costCardObjectUid: Int? = null
    var action: Action? = null

    var options: ActionOptions? = null

    private val actionCreators = listOf(
        ActionCreator("loan") { user, costCard -> LoanAction(user, costCard) },
    )

    fun update() {
        val costCardObjectUid = costCardObjectUid
        val simulator = birminghamGamer.game.simulator
        val costCard =
            if (costCardObjectUid != null) simulator.gameObjects[costCardObjectUid]!!.getBehaviorByType(CardBehavior.TYPE)!!.card else null
        val action = action
        if (costCard == null) {
            options = ActionOptions(
                "选择一张手牌：",
                birminghamGamer.cardObjects.map { cardObject ->
                    val card = cardObject.getBehaviorByType(CardBehavior.TYPE)!!.card!!
                    ActionOption(card.name) { this.costCardObjectUid = cardObject.uid }
                }
            )
        } else if (action == null) {
            options = ActionOptions(
                "选择以下行动之一：",
                actionCreators.map { creator ->
                    ActionOption(creator.name) {
                        this.action = creator.create(birminghamGamer, costCard)
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
        val function = options?.options?.getOrNull(index)?.callback
        if (function != null) {
            function()
            update()
        }

        birminghamGamer.game.listenerGameStateUpdated.emit(birminghamGamer.game)
        birminghamGamer.game.listenerActionOptionsUpdated.emit(birminghamGamer)
    }

    fun reset() {
        costCardObjectUid = null
        action = null
        options = null

        birminghamGamer.game.listenerGameStateUpdated.emit(birminghamGamer.game)
        birminghamGamer.game.listenerActionOptionsUpdated.emit(birminghamGamer)
    }
}