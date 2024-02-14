package io.github.theriverelder.minigames.tablebottomsimulator.simulator.user

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*

class Gamer(
    val simulator: TableBottomSimulatorServer,
    val uid: Int,
    var home: Vector2 = Vector2.zero(),
    var userUid: Int? = null,
    var color: String = "white",
    var cardObjectUidList: List<Int> = emptyList(),
) : Persistable {

    var user: User?
        get() {
            return simulator.users[userUid ?: return null]
        }
        set(value) {
            user?.gamerUid = null
            userUid = value?.uid
            value?.gamer?.userUid = null
            value?.gamerUid = uid
        }


    override fun save(): JsonObject = buildJsonObject {
        put("uid", uid)
        put("home", home.save())
        put("userUid", userUid)
        put("color", color)
        put("cardObjectUidList", buildJsonArray { cardObjectUidList.forEach { add(it) } })
    }

    override fun restore(data: JsonObject) {
        home = restoreVector2(data.forceGet("home").jsonObject)
        userUid = data.forceGet("userUid").jsonPrimitive.intOrNull
        color = data.forceGet("color").jsonPrimitive.content
        cardObjectUidList = data["cardObjectUidList"]?.jsonArray?.map { it.jsonPrimitive.int } ?: emptyList()
    }

    // 提取信息，只有自己可以看到自己的手牌
    fun extractData(user: User? = null): JsonObject = buildJsonObject {
        put("uid", uid)
        put("home", home.save())
        put("userUid", userUid)
        put("color", color)
        put("cardAmount", cardObjectUidList.size)
        if (user != null && userUid == user.uid) put(
            "cardObjectUidList",
            buildJsonArray { cardObjectUidList.forEach { add(it) } })
    }

     val cards: List<Card> get() = cardObjects.map { it.getBehaviorByType(CardBehavior.TYPE)!!.card!! }

    val cardObjects: List<GameObject> get() = cardObjectUidList.map { simulator.gameObjects[it]!! }

    fun removeCardFromHand(vararg targetUidList: Int) {
        var removed = false
        val newList = arrayListOf<Int>()
        for (uid in cardObjectUidList) {
            if (targetUidList.contains(uid)) {
                removed = true
                continue
            }
            newList += uid
        }
        if (removed) {
            cardObjectUidList = newList.toList()
            simulator.channelGamePlayer.sendGamers()
        }
    }

    fun addCardToHand(vararg targetUidList: Int) {
        var added = false
        for (uid in targetUidList) {
            if (!cardObjectUidList.contains(uid)) {
                cardObjectUidList += uid
                added = true
            }
        }
        if (added) simulator.channelGamePlayer.sendGamers()
    }
}

fun restoreGamer(data: JsonObject, simulator: TableBottomSimulatorServer): Gamer {
    val uid = data.forceGet("uid").jsonPrimitive.int
    val gamer = Gamer(simulator, uid)
    gamer.restore(data)
    return gamer
}