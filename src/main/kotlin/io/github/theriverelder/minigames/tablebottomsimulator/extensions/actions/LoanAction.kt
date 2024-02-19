package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionBase
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOption
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOptions

class LoanAction(val birminghamGamer: BirminghamGamer, costCardObjectUid: Int) : ActionBase(birminghamGamer.user!!, costCardObjectUid) {

    override val options: ActionOptions get() = ActionOptions("确认耻辱贷款", listOf(ActionOption("确认") { }))

    override fun reset() { }

    override val fulfilled: Boolean get() = true

    override fun perform() {
        birminghamGamer.money += 30
        // TODO 降低收入轨
    }
}