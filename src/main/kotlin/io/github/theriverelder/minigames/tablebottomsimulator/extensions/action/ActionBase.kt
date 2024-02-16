package io.github.theriverelder.minigames.tablebottomsimulator.extensions.action

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User

abstract class ActionBase(
    val user: User,
    val costCard: Card,
) : Action