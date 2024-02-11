package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.Extension
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardSeries

class BirminghamExtension(
    var simulator: TableBottomSimulatorServer,
) : Extension {

    override val name: String get() = "birmingham"

    var channel: BirminghamInstructionChannel

    var birminghamGame: BirminghamGame? = null

    val listenerGameCreated = ListenerManager<BirminghamGame>()

    lateinit var cardSeriesCard: CardSeries
    lateinit var cardSeriesFactory: CardSeries

    init {
        run {
            val cardSeries =
                CardSeries("birmingham_card", "http://localhost:8089/minigames/birmingham/image/card/card_back.jpg")
            createAndAddCard(cardSeries, CARD_NAMES)
            CardSeries.SERIES.add(cardSeries)
            cardSeriesCard = cardSeries
        }
        run {
            val cardSeries = CardSeries("birmingham_factory", "")
            FACTORY_SET.forEach { data ->
                val typeName = data.first
                repeat(data.second.size) { level ->
                    val cardName = "${typeName}_level_${level.toString().padStart(2, '0')}"
                    val card = Card(
                        cardName,
                        cardSeries,
                        "http://localhost:8089/minigames/birmingham/image/factory/${cardName}_face.jpg",
                        "http://localhost:8089/minigames/birmingham/image/factory/${cardName}_back.jpg",
                    )
                    cardSeries.cards.add(card)
                }
            }
            CardSeries.SERIES.add(cardSeries)
            cardSeriesFactory = cardSeries
        }

        // 主要桌布
        val mapObject = simulator.createAndAddGameObject()
        mapObject.size = Vector2(4000.0, 4000.0)
        mapObject.position = Vector2.zero()
        mapObject.background = "http://localhost:8089/minigames/birmingham/image/common/map.jpg"
        mapObject.shape = "rectangle"

        // 测试卡牌
        val cardGameObject = simulator.createAndAddGameObject()
        cardGameObject.shape = "rectangle"
        cardGameObject.size = Vector2(500.0, 702.0)
        val cardBehavior = cardGameObject.createAndAddBehavior(CardBehavior.TYPE)
        cardBehavior.series = cardSeriesCard
        cardBehavior.card = cardSeriesCard.cards["birmingham"]

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