package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardSeries
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.sendCommand
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.serialization.json.*

class CardChannel(simulator: TableBottomSimulatorServer) : Channel("card", simulator) {
    override fun receive(data: JsonObject, sender: User) {

        when (data.forceGet("action").jsonPrimitive.content) {
            "request_all" -> sendCardSerieses(sender, CardSeries.SERIES.values)
        }
    }

    fun sendCardSerieses(receiver: User? = null, cardSerieses: Collection<CardSeries> = CardSeries.SERIES.values) {
        sendCommand(receiver, "update_serieses", JsonArray(cardSerieses.map { it.save() }))
    }
    fun sendCards(receiver: User? = null, cards: Collection<Card>) {
        sendCommand(receiver, "update_cards", JsonArray(cards.map { it.save() }))
    }
}