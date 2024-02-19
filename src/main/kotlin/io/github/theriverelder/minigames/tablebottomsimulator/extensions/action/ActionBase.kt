package io.github.theriverelder.minigames.tablebottomsimulator.extensions.action

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.card
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User

abstract class ActionBase(
    val user: User,
    val costCardObjectUid: Int,
) : Action {

    val costCard: Card get() = user.simulator.gameObjects[costCardObjectUid]!!.card
}