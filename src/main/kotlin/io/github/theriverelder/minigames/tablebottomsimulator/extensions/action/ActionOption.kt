package io.github.theriverelder.minigames.tablebottomsimulator.extensions.action

data class ActionOption(
    val text: String,
    val callback: () -> Unit,
)
