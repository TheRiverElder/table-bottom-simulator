package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.ActionGuide
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.json.*

class BirminghamGamer(
    val game: BirminghamGame,
    val ordinal: Int,
    var userUid: Int? = null,
    var money: Int = 0,
    var cardObjectUidList: List<Int> = emptyList(),
) : Persistable {

    var actionGuide: ActionGuide? = null

    var user: User
        get() {
            val userUid = this.userUid ?: throw Exception("No user occupied")
            return game.simulator.users[userUid] ?: throw Exception("Cannot find user with uid: $userUid")
        }
        set(value) {
            userUid = value.uid
            game.listenerGameStateUpdated.emit(game)
        }


    val userOrNull: User?
        get() {
            val userUid = this.userUid ?: return null
            return game.simulator.users[userUid]
        }

    override fun save(): JsonObject= buildJsonObject {
        put("ordinal", JsonPrimitive(ordinal))
        put("userUid", JsonPrimitive(userUid))
        put("money", JsonPrimitive(money))
        put("cardObjectUidList", buildJsonArray { cardObjectUidList.forEach { add(it) } })
    }

    override fun restore(data: JsonObject) {
        cardObjectUidList = data["cardObjectUidList"]?.jsonArray?.map { it.jsonPrimitive.int } ?: emptyList()
    }

    val cards: List<Card> get() = cardObjects.map { it.getBehaviorByType(CardBehavior.TYPE)!!.card!! }

    val cardObjects: List<GameObject> get() = cardObjectUidList.map { game.simulator.gameObjects[it]!! }
}

fun restoreBirminghamGamer(data: JsonObject, game: BirminghamGame): BirminghamGamer {
    val gamer = BirminghamGamer(
        game,
        data.forceGet("ordinal").jsonPrimitive.int,
        data.forceGet("userUid").jsonPrimitive.int,
        data.forceGet("money").jsonPrimitive.int,
    )
    gamer.restore(data)
    return gamer
}