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
            val prefix = "http://localhost:8089/minigames/birmingham/image/common/cards/"
            val cardSeries =
                CardSeries("birmingham_card", prefix + "card_back.jpg")

            for (name in CARD_NAMES) {
                cardSeries.cards.add(Card(name, cardSeries,  prefix + "${name}.jpg"))
            }
            CardSeries.SERIES.add(cardSeries)
            cardSeriesCard = cardSeries
        }
        run {
            val prefix = "http://localhost:8089/minigames/birmingham/image/gamers/"
            val cardSeries = CardSeries("birmingham_factory", "")
            GAMER_COLORS.forEach { gamerColor ->
                FACTORY_SET.forEach { data ->
                    val typeName = data.first
                    repeat(data.second.size) { level ->
                        val subName = "${typeName}_level_${level.toString().padStart(2, '0')}"
                        val cardName = "${gamerColor}_${subName}"
                        val fileName = "${typeName}_level_${(level + 1).toString().padStart(2, '0')}"
//                        println(fileName)
                        val card = Card(
                            cardName,
                            cardSeries,
                            prefix + "${gamerColor}/${fileName}_face.jpg",
                            prefix + "${gamerColor}/${fileName}_back.jpg",
                        )
                        cardSeries.cards.add(card)
                    }
                }
            }
            CardSeries.SERIES.add(cardSeries)
            cardSeriesFactory = cardSeries
        }

        simulator.channelCard.sendCardSerieses()

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

val GAMER_COLORS = listOf(
    "orange",
    "yellow",
    "teal",
    "purple",
)