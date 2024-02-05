package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamUser

class LoanAction(val birminghamUser: BirminghamUser, costCard: Card) : ActionBase(birminghamUser.simulatorUser, costCard) {

    override val options: ActionOptions get() = ActionOptions("确认耻辱贷款", listOf(ActionOption("确认", { })))

    override fun reset() { }

    override val fulfilled: Boolean get() = true

    override fun perform() {
        birminghamUser.money += 30
        // TODO 降低收入轨
    }
}