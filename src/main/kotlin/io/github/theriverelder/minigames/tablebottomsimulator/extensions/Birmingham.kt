package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardSeries

// 只是注册一些主要的BehaviorType
fun initializeBirmingham(simulator: TableBottomSimulatorServer) {
    val extension = BirminghamExtension(simulator)
}

val CARD_NAMES = listOf(
    "any",
    "belper",
    "birmingham",
    "brewery",
    "burton_upon_trent",
    "cannock",
    "coal_mine",
    "coalbrookdale",
    "coventry",
    "cutton_mill_manufacturer",
    "derby",
    "iron_works",
    "kidderminster",
    "leek",
    "manufacturer_cutton_mill",
    "manufacturer_cutton_mill_2",
    "nuneaton",
    "pottery",
    "redditch",
    "stafford",
    "stoke_on_trent",
    "stone",
    "tamworth",
    "uttoxeter",
    "walsall",
    "wild",
    "wolverhampton",
    "worcester",
)

fun createAndAddCard(series: CardSeries, names: Collection<String>) {
    for (name in names) {
        val face = "http://localhost:8089/minigames/birmingham/image/common/cards/${name}.jpg"
        val card = Card(name, series, face)
        series.cards.add(card)
    }
}