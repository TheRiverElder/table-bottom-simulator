package io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject

enum class Side(
    val activeOnServer: Boolean,
    val activeOnClient: Boolean,
) {
    CLIENT(false, true),
    SERVER(true, false),
    BOTH(true, true),
    NONE(false, false),
}