package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardSeries


// 只是注册一些主要的BehaviorType
fun initializeBirmingham(simulator: TableBottomSimulatorServer) {
    val cardSeries = CardSeries("birmingham", "purple")
    createAndAddCard(cardSeries, listOf(
        "any",
        "belper",
        "brimingham",
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
    ))
    CardSeries.SERIES.add(cardSeries);
}

fun createAndAddCard(series: CardSeries, names: Collection<String>) {
    for (name in names) {
        val face = "http://localhost:8089/minigames/birmingham/images/common/cards/${name}.jpg"
        val card = Card(name, series, face)
        series.cards.add(card)
    }
}