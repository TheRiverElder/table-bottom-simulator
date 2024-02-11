package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardSeries
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.ActionGuide
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.user.Gamer
import kotlinx.serialization.json.*
import java.lang.System.currentTimeMillis
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
        simulator.gamers.clear()

        val random = Random(currentTimeMillis())
        val cardSeries =  extension.cardSeriesCard

        // 根据人数生成对应数量的牌组
        var cardGameObjectUidList = CARD_SET_BY_PLAYER_AMOUNT.flatMap<Pair<String, List<Int>>, Int> { data ->
            val cardName = data.first
            val amount = data.second[gamerAmount - 2]
//            println("Card: ${cardName}")
            val card = cardSeries.cards[cardName]!!
            buildList(amount) {
                val gameObject = simulator.gameObjects.addRaw { GameObject(simulator, it) }
                gameObject.card = card
                gameObject.size = Vector2(500.0, 702.0)
                gameObject.shape = "rectangle"
                gameObject.sendUpdateSelf()
                gameObject.uid
            }
        }.shuffled()

        // 分牌
        val cardAmount = ROUNDS_OF_EACH_ERA_BY_PLAYER_AMOUNT.get(gamerAmount - 2)
        for (index in 0 until gamerAmount) {
//            println("gamer ${index}")
            val gamer = simulator.gamers.addRaw { Gamer(simulator, it) }
            gamer.home = Vector2(3000.0, -3000.0 + 1200 * index)
            gamer.cardObjectUidList = cardGameObjectUidList.take(cardAmount)
            cardGameObjectUidList = cardGameObjectUidList.drop(cardAmount)

            val birminghamGamer = BirminghamGamer(this, gamer.uid, index)
            birminghamGamer.initialize()
            gamerList.add(birminghamGamer)
        }
        this.cardGameObjectUidList = cardGameObjectUidList

        prepareUsers()
    }

    fun step() {
        currentOrdinal = (currentOrdinal + 1) % gamerList.size
        prepareUsers()
    }

    fun prepareUsers() {
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
        put("period", period)
        put("currentOrdinal", currentOrdinal)
        put("gamerList", buildJsonArray { gamerList.forEach { add(it.save()) } })
    }

    override fun restore(data: JsonObject) {
        period = data.forceGet("period").jsonPrimitive.int
        currentOrdinal = data.forceGet("currentOrdinal").jsonPrimitive.int
        gamerList.clear()
        data.forceGet("gamerList").jsonArray.forEach { gamerData ->
            gamerList.add(restoreBirminghamGamer(gamerData.jsonObject, this))
        }
    }

}

var GameObject.card: Card
    get() = getBehaviorByType(CardBehavior.TYPE)!!.card!!
    set(value) {
        val behavior = getBehaviorByType(CardBehavior.TYPE) ?: createAndAddBehavior(CardBehavior.TYPE)
        behavior.card = value
    }

val CARD_SET_BY_PLAYER_AMOUNT = listOf(
    // 青色
    "belper" to listOf(0, 0, 2),
    "derby" to listOf(0, 0, 3),
    // 蓝色
    "leek" to listOf(0, 2, 2),
    "stoke_on_trent" to listOf(0, 3, 3),
    "stone" to listOf(0, 2, 2),
    "uttoxeter" to listOf(0, 1, 2),
    // 红色
    "stafford" to listOf(2, 2, 2),
    "burton_on_trent" to listOf(2, 2, 2),
    "cannock" to listOf(2, 2, 2),
    "tamworth" to listOf(1, 1, 1),
    "walsall" to listOf(1, 1, 1),
    // 黄色
    "coalbrookdale" to listOf(3, 3, 3),
    "dudley" to listOf(2, 2, 2),
    "kidderminster" to listOf(2, 2, 2),
    "wolverhampton" to listOf(2, 2, 2),
    "worcester" to listOf(2, 2, 2),
    // 紫色
    "birmingham" to listOf(3, 3, 3),
    "coventry" to listOf(3, 3, 3),
    "nuneaton" to listOf(1, 1, 1),
    "redditch" to listOf(1, 1, 1),
    // 产业牌
    "iron_works" to listOf(4, 4, 4),
    "coal_mine" to listOf(2, 2, 3),
    "cotton_or_manufacturer" to listOf(0, 6, 8),
    "pottery" to listOf(2, 2, 3),
    "brewery" to listOf(5, 5, 5),
)

val ROUNDS_OF_EACH_ERA_BY_PLAYER_AMOUNT = listOf(10, 9, 8)