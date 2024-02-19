package io.github.theriverelder.minigames.tablebottomsimulator.extensions.action

import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer

class ActionCreator(
    val name: String,
    val create: (BirminghamGamer, Int) -> Action,
)