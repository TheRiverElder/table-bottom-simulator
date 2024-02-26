package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

class CityGroup(val name: String): GraphNode<Network, BirminghamGraphNodeContext> {

    var cities: List<City> = emptyList()

    override var neighbors: List<Network> = emptyList()

    override fun canPass(context: BirminghamGraphNodeContext): Boolean = true

}
