package io.github.theriverelder.minigames.tablebottomsimulator.extensions

class FactoryInfo(
    val typeName: String,
    val level: Int,
    val amount: Int,
    val costs: List<Pair<String, Int>>,
    val award: List<Pair<String, Int>>,
) {
}