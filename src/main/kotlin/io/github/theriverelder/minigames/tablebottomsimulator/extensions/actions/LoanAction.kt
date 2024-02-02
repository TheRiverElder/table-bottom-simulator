package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.user.User

class LoanAction(user: User, costCard: Card) : ActionBase(user, costCard) {

    override var options: List<ActionOption> = emptyList()

    override fun update() {
        options = emptyList()
    }

    override fun reset() { }

    override fun perform() {
        TODO()
    }
}