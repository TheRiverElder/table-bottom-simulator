package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

interface Action {
    var options: List<Function<Unit>>
    fun update()
    fun reset()
}