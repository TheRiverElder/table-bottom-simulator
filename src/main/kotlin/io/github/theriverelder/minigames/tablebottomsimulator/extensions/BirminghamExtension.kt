package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.Extension
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardSeries

class BirminghamExtension(
    var simulator: TableBottomSimulatorServer,
) : Extension {

    override val name: String get() = "birmingham"

    var channel: BirminghamInstructionChannel

    var birminghamGame: BirminghamGame? = null

    val listenerGameCreated = ListenerManager<BirminghamGame>()

    init {

        val cardSeries = CardSeries("birmingham", "http://localhost:8089/minigames/birmingham/image/common/card_back.jpg")
        createAndAddCard(cardSeries, CARD_NAMES)
        CardSeries.SERIES.add(cardSeries)

        val mapObject = simulator.createAndAddGameObject()
        mapObject.size = Vector2(4000.0, 4000.0)
        mapObject.position = Vector2.zero()
        mapObject.background = "http://localhost:8089/minigames/birmingham/image/common/map.jpg"
        mapObject.shape = "rectangle"

        val cardGameObject = simulator.createAndAddGameObject()
        cardGameObject.shape = "rectangle"
        cardGameObject.size = Vector2(500.0, 702.0)
        val cardBehavior = cardGameObject.createAndAddBehavior(CardBehavior.TYPE)
        cardBehavior.series = cardSeries
        cardBehavior.card = cardSeries.cards["birmingham"]

        channel = BirminghamInstructionChannel(this)
        simulator.channels.add(channel)


    }

    fun createGame(gamerAmount: Int) {
        val birminghamGame = BirminghamGame(this, gamerAmount)
        this.birminghamGame = birminghamGame
        birminghamGame.initialize()
        listenerGameCreated.emit(birminghamGame)
    }
}