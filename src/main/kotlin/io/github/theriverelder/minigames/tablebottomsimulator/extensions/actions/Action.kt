package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

interface Action {
    var options: List<ActionOption>
    fun update()
    fun reset()
    fun perform()
}