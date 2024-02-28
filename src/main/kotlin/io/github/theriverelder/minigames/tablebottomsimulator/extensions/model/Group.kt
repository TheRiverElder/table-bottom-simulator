package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

class Group<T, N>(val name: String): GraphNode<N, BirminghamGraphNodeContext> {

    var content: List<T> = emptyList()

    override var neighbors: List<N> = emptyList()

    override fun canPass(context: BirminghamGraphNodeContext): Boolean = true

}
