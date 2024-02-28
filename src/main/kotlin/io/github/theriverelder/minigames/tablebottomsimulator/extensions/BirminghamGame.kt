package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel.UpdateGameObjectSelfOptions
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionGuide
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*
import java.lang.System.currentTimeMillis
import kotlin.math.PI
import kotlin.random.Random

class BirminghamGame(
    val extension: BirminghamExtension,
    val gamerAmount: Int,
) : Persistable {
    val simulator: TableBottomSimulatorServer get() = extension.simulator

    val gamerList: MutableList<BirminghamGamer> = ArrayList(4)

    var currentOrdinal: Int = 0

    var cardGameObjectUidList = emptyList<Int>()


    fun getGamerByUserUid(uid: Int): BirminghamGamer? {
        val gamerUid = simulator.users.values.find { it.uid == uid }?.gamer?.uid
        return if (gamerUid == null) null else gamerList.find { it.gamer?.uid == gamerUid }
    }


    /**
     * 0 还没开始
     * 1 运河时代
     * 2 铁路时代
     * 3 终局
     */
    var period: Int = 0

    val currentUser: BirminghamGamer
        get() = gamerList.find { it.ordinal == currentOrdinal }
            ?: throw Exception("Cannot find user with ordinal: $currentOrdinal")

    fun initialize() {
        period = 1

        simulator.gamers.clear()

        val random = Random(currentTimeMillis())
        val cardSeries = extension.cardSeriesCard

        println("Initializing game: $gamerAmount gamers")

        // 根据人数生成对应数量的牌组
        var cardGameObjectUidList = CARD_SET_BY_PLAYER_AMOUNT.flatMap { data ->
            val cardName = data.first
            val amount = data.second[gamerAmount - 2]

            println("Create card game objects: ${cardName}, at amount $amount")

            if (amount <= 0) return@flatMap emptyList<Int>()

            val card = cardSeries.cards[cardName]!!
            buildList(amount) {
                repeat(amount) {
                    val gameObject = simulator.gameObjects.addRaw { GameObject(simulator, it) }
                    gameObject.position = Vector2(-1474, -1370)
                    gameObject.rotation = PI / 2
                    gameObject.card = card
                    gameObject.shape = "rectangle"
                    gameObject.sendUpdateFull()
                    add(gameObject.uid)
                }
            }
        }.shuffled(random)

        println("Create card set: ${cardGameObjectUidList.size} cards")

        // 分牌
        val gamers = createGamers()
        val cardAmount = ROUNDS_OF_EACH_ERA_BY_PLAYER_AMOUNT[gamerAmount - 2]
        for (ordinal in 0 until gamerAmount) {
//            println("gamer ${index}")
            val gamer = gamers[ordinal]

            gamer.cardObjectUidList = cardGameObjectUidList.take(cardAmount)
            cardGameObjectUidList = cardGameObjectUidList.drop(cardAmount)

            val birminghamGamer = BirminghamGamer(this, gamer.uid, ordinal)
            birminghamGamer.initialize()
            gamerList.add(birminghamGamer)
        }
        this.cardGameObjectUidList = cardGameObjectUidList

        prepareGamers()
    }

    fun step() {
        currentOrdinal = (currentOrdinal + 1) % gamerList.size
        prepareGamers()
    }

    fun createGamers(): List<Gamer> {
        return buildList(gamerAmount) {
            for (ordinal in 0 until gamerAmount) {
                val gamer = simulator.gamers.addRaw { Gamer(simulator, it) }
                gamer.color = GAMER_COLORS[ordinal]
                gamer.home = when (ordinal) {
                    0 -> Vector2(3000, -4000)
                    1 -> Vector2(3000, 0)
                    2 -> Vector2(-7000, -4000)
                    3 -> Vector2(-7000, 0)
                    else -> Vector2(10000, 10000)
                }
                add(gamer)
            }
        }
    }

    fun prepareGamers() {
        for (birminghamGamer in gamerList) {
            birminghamGamer.actionGuide =
                if (birminghamGamer.ordinal == currentOrdinal) ActionGuide(birminghamGamer) else null
            birminghamGamer.actionGuide?.update()
            listenerActionOptionsUpdated.emit(birminghamGamer)
        }
        listenerGameStateUpdated.emit(this)
    }

    val listenerGameStateUpdated = ListenerManager<BirminghamGame>()
    val listenerActionOptionsUpdated = ListenerManager<BirminghamGamer>()

    override fun save(): JsonObject = buildJsonObject {
        put("gamerAmount", gamerAmount)
        put("period", period)
        put("currentOrdinal", currentOrdinal)
        put("gamerList", buildJsonArray { gamerList.forEach { add(it.save()) } })
        put("cardGameObjectUidList", cardGameObjectUidList.save())
    }

    fun extractData(): JsonObject = buildJsonObject {
        put("gamerAmount", gamerAmount)
        put("period", period)
        put("currentOrdinal", currentOrdinal)
        put("gamerList", buildJsonArray { gamerList.forEach { add(it.extractData()) } })
        put("cardGameObjectUidList", cardGameObjectUidList.save())
    }

    override fun restore(data: JsonObject) {
        period = data.forceGet("period").jsonPrimitive.int
        currentOrdinal = data.forceGet("currentOrdinal").jsonPrimitive.int
        gamerList.clear()
        data.forceGet("gamerList").jsonArray.forEach { gamerData ->
            gamerList.add(restoreBirminghamGamer(gamerData.jsonObject, this))
        }
        cardGameObjectUidList = data.forceGet("cardGameObjectUidList").jsonArray.map { it.jsonPrimitive.int }

        // 额外的逻辑
//        createGamers()
        prepareGamers()
    }


//        for (networkPair in networkGameObjects) {
//            val gameObject = networkPair.first
//            val rawNetwork = networkPair.second
//
//            val position = gameObject.position
////            val direction = gameObject.rotation
//
//            val cityNamePairs = Stack<Pair<String, Double>>()
//
//            for (cityPair in cityGameObjects) {
//                val cityPosition = cityPair.first.position
//
//                val distanceSquared = (cityPosition - position).modSquared
//
//                val p = cityPair.second.name to distanceSquared
//                if (cityNamePairs.size < 2) {
//                    cityNamePairs.add(p)
//                    continue
//                }
//
//                cityNamePairs.add(p)
//                cityNamePairs.sortByDescending { it.second }
//
//                while (cityNamePairs.size > 2) {
//                    cityNamePairs.pop()
//                }
//            }
//
//            val cityNames = cityNamePairs.map { it.first }
//            val network = Network((cityNames + rawNetwork.cityNames).toSet().toList(), rawNetwork.periods, rawNetwork.placeholderObjectUid)
//            gameObject.network = network
//        }

    fun discardGameObject(gameObject: GameObject) {
        gameObject.position = Vector2(0, -3000)
        gameObject.sendUpdateSelf(UpdateGameObjectSelfOptions(position = true))
    }
}

fun restoreBirminghamGame(data: JsonObject, extension: BirminghamExtension): BirminghamGame {
    val game = BirminghamGame(
        extension,
        data.forceGet("gamerAmount").jsonPrimitive.int,
    )
    game.restore(data)
    return game
}

var GameObject.card: Card
    get() = getBehaviorByType(CardBehavior.TYPE)!!.card!!
    set(value) {
        val behavior = getOrCreateAndAddBehaviorByType(CardBehavior.TYPE)
        behavior.card = value
    }
