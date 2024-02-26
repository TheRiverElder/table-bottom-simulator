package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

interface GraphNode<N, C> {
//    val identity: K
    val neighbors: List<N>

    fun canPass(context: C): Boolean
}