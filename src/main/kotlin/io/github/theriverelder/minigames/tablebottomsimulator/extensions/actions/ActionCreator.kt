package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer

class ActionCreator(
    val name: String,
    val create: (BirminghamGamer, Card) -> Action,
)