package io.github.theriverelder.minigames.tablebottomsimulator.extensions.action

interface Action {
    val options: ActionOptions?

    fun reset()
    val fulfilled: Boolean
    fun perform()
}