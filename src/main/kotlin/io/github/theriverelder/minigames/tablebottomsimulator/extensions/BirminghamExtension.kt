package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardSeries
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel.UpdateGameObjectSelfOptions
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.Extension
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.Gamer
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

class BirminghamExtension(
    var simulator: TableBottomSimulatorServer,
) : Extension {

    override val name: String get() = "birmingham"

    var channel: BirminghamInstructionChannel

    var birminghamGame: BirminghamGame? = null
    var birminghamMap = BirminghamMap(this)

    val listenerGameCreated = ListenerManager<BirminghamGame>()

    lateinit var cardSeriesCard: CardSeries
    lateinit var cardSeriesFactory: CardSeries
    lateinit var cardSeriesNetwork: CardSeries

    init {
        run {
            val prefix = "http://localhost:8089/minigames/birmingham/image/common/cards/"
            val cardSeries =
                CardSeries("birmingham:card", prefix + "card_back.jpg", Vector2(400, 560))
//                CardSeries("birmingham_card", prefix + "card_back.jpg", Vector2(500, 702))

            for (name in (CARD_SET_BY_PLAYER_AMOUNT.map { it.first } + listOf("any", "wild"))) {
                cardSeries.cards.add(Card(name, cardSeries, prefix + "${name}.jpg"))
            }
            CardSeries.SERIES.add(cardSeries)
            cardSeriesCard = cardSeries
        }
        run {
            val prefix = "http://localhost:8089/minigames/birmingham/image/gamers/"
            val cardSeries = CardSeries("birmingham:factory", "", Vector2(180, 180))
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
        run {
            val prefix = "http://localhost:8089/minigames/birmingham/image/gamers/"
            val cardSeries = CardSeries("birmingham:network", "", Vector2(190, 80))
            GAMER_COLORS.forEach { gamerColor ->
                cardSeries.cards.add(Card("${gamerColor}_canal", cardSeries, prefix + "${gamerColor}/canal.png"))
                cardSeries.cards.add(Card("${gamerColor}_rail", cardSeries, prefix + "${gamerColor}/rail.png"))
            }
            CardSeries.SERIES.add(cardSeries)
            cardSeriesNetwork = cardSeries
        }

        simulator.channelCard.sendCardSerieses()


        channel = BirminghamInstructionChannel(this)
        simulator.channels.add(channel)


    }

    override fun initialize() {
        // 主要桌布
        val mapObject = simulator.createAndAddGameObject()
        mapObject.size = Vector2(4000.0, 4000.0)
        mapObject.position = Vector2.zero()
        mapObject.background = "http://localhost:8089/minigames/birmingham/image/common/map.jpg"
        mapObject.shape = "rectangle"

//        // 测试卡牌
//        val cardGameObject = simulator.createAndAddGameObject()
//        cardGameObject.shape = "rectangle"
//        cardGameObject.size = Vector2(500.0, 702.0)
//        val cardBehavior = cardGameObject.createAndAddBehavior(CardBehavior.TYPE)
//        cardBehavior.series = cardSeriesCard
//        cardBehavior.card = cardSeriesCard.cards["birmingham"]
    }

    override fun save(): JsonObject = buildJsonObject {
        put("birminghamGame", birminghamGame?.save() ?: JsonNull)
        put("birminghamMap", birminghamMap.save())
    }

    override fun restore(data: JsonObject) {
        data["birminghamGame"]?.let { dataElement ->
            if (dataElement == JsonNull) return@let
            val birminghamGame = restoreBirminghamGame(dataElement.jsonObject, this)
            this.birminghamGame = birminghamGame
            listenerGameCreated.emit(birminghamGame)
        }
        data["birminghamMap"]?.let {
            if (it == JsonNull) return@let
            this.birminghamMap.restore(it.jsonObject)
        }
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

fun Gamer.cleanupCards(gap: Double = 10.0): List<GameObject> {
    val gameObjects = cardObjectUidList.mapNotNull { simulator.gameObjects[it] }
    var xCounter = 0.0
    for (gameObject in gameObjects) {
        gameObject.position = home + Vector2(xCounter, 0)
        xCounter += gameObject.size.x + gap
        gameObject.sendUpdateSelf(UpdateGameObjectSelfOptions(position = true))
    }

    return gameObjects
}