package io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel

data class UpdateGameObjectSelfOptions(
    val position: Boolean = true,
    val size: Boolean = true,
    val rotation: Boolean = true,
    val background: Boolean = true,
    val shape: Boolean = true,
    val tags: Boolean = true,
)
