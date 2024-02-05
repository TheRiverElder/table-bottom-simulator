package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamUser

class ActionCreator(
    val name: String,
    val create: (BirminghamUser, Card) -> Action,
)