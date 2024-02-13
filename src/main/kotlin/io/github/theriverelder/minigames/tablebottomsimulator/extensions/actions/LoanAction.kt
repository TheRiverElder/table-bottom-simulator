package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer

class LoanAction(val birminghamGamer: BirminghamGamer, costCard: Card) : ActionBase(birminghamGamer.user!!, costCard) {

    override val options: ActionOptions get() = ActionOptions("确认耻辱贷款", listOf(ActionOption("确认") { }))

    override fun reset() { }

    override val fulfilled: Boolean get() = true

    override fun perform() {
        birminghamGamer.money += 30
        // TODO 降低收入轨
    }
}