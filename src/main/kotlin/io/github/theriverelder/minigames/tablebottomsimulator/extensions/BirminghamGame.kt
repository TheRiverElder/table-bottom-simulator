package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
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
        val cardCandidates = CardSeries.SERIES["birmingham"]!!.cards

        for (index in 0 until gamerAmount) {
//            println("gamer ${index}")
            val gamer = simulator.gamers.addRaw { Gamer(simulator, it) }
            gamer.home = Vector2(3000.0, -3000.0 + 1000 * index)
            val cardUidList = (0 until 6).map { i ->
//                println("card ${i} of ${index}")
                val gameObject = simulator.gameObjects.addRaw { GameObject(simulator, it) }
                gameObject.size = Vector2(500.0, 702.0)
                gameObject.shape = "rectangle"
                gameObject.position = gamer.home + Vector2(300.0 * i, 0.0)

                val cardBehavior = gameObject.createAndAddBehavior(CardBehavior.TYPE)
                cardBehavior.card = cardCandidates[CARD_NAMES[random.nextInt(0, cardCandidates.size)]]

                gameObject.sendUpdateFull()
//                println(gameObject.uid)
                gameObject.uid
            }
            gamer.cardObjectUidList = cardUidList

            val birminghamGamer = BirminghamGamer(this, gamer.uid, index)
            gamerList.add(birminghamGamer)
        }

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