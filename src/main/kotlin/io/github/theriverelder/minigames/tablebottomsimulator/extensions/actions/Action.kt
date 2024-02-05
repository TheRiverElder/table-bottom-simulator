package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

interface Action {
    val options: ActionOptions?

    fun reset()
    val fulfilled: Boolean
    fun perform()
}