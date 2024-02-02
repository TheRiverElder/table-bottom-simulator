package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.user.User

abstract class ActionGuide(val user: User) : Action {

    var costCard: Card? = null

    override var options: List<ActionOption> = emptyList()

    override fun update() {
        val costCard = costCard
        options = if (costCard == null) user.cards.map { card -> ActionOption(
            card.name,
            { this.costCard = card },
        ) } else getFollowingOptions(costCard)
    }

    override fun reset() {
        costCard = null
    }

    abstract fun getFollowingOptions(costCard: Card): List<ActionOption>
}