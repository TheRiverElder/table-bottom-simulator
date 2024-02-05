package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

data class ActionOption(
    val text: String,
    val callback: () -> Unit,
)
