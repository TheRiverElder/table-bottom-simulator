package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardSeries
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.ActionGuide
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.genUid
import java.lang.System.currentTimeMillis
import kotlin.random.Random

class BirminghamGame(
    val extension: BirminghamExtension,
    val gamerAmount: Int,
) {
    val simulator: TableBottomSimulatorServer get() = extension.simulator

    val gamerList: MutableList<BirminghamGamer> = ArrayList(4)

    fun getGamerByUserUid(uid: Int): BirminghamGamer? {
        return gamerList.find { it.userUid == uid }
    }

    var currentOrdinal: Int = 0

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
        val random = Random(currentTimeMillis())
        val cardCandidates = CardSeries.SERIES["birmingham"]!!.cards

        (0 until gamerAmount).forEach { index ->
            val gamer = BirminghamGamer(this, index)
            val cardUidList = (0 until 10).map {
                val gameObject = GameObject(simulator, simulator.genUid())
                gameObject.size = Vector2(500.0, 702.0)
                gameObject.position = Vector2(-100.0, -100.0)

                val cardBehavior = gameObject.createAndAddBehavior(CardBehavior.TYPE)
                cardBehavior.card = cardCandidates[CARD_NAMES[random.nextInt(0, cardCandidates.size)]]

                simulator.gameObjects.add(gameObject)
                gameObject.uid
            }
            gamer.cardObjectUidList = cardUidList
            gamerList.add(gamer)
        }
        prepareUser()
    }

    fun step() {
        currentOrdinal = (currentOrdinal + 1) % gamerList.size
        prepareUser()
    }

    fun prepareUser() {
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

    fun getNotOccupiedGamers(): List<BirminghamGamer> = gamerList.filter { it.userUid == null }
}