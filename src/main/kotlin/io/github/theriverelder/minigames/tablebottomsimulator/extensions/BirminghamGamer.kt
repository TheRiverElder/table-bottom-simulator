package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.ActionGuide
import io.github.theriverelder.minigames.tablebottomsimulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.json.*

class BirminghamGamer(
    val game: BirminghamGame,
    val gamerUid: Int,
    val ordinal: Int,
    var money: Int = 0,
) : Persistable {

    var actionGuide: ActionGuide? = null

    val gamer: Gamer?
        get() = game.simulator.gamers[gamerUid]

    var user: User?
        get() = gamer?.user
        set(value) {
            gamer?.user = value
        }

    override fun save(): JsonObject = buildJsonObject {
        put("gamerUid", gamer?.uid)
        put("ordinal", ordinal)
        put("money", money)
    }

    fun extractData(): JsonObject = buildJsonObject {
        put("gamerUid", gamer?.uid)
        put("ordinal", ordinal)
        put("money", money)
    }

    override fun restore(data: JsonObject) {
        money = data.forceGet("money").jsonPrimitive.int
    }
}

fun restoreBirminghamGamer(data: JsonObject, game: BirminghamGame): BirminghamGamer {
    val gamer = BirminghamGamer(
        game,
        data.forceGet("gamerUid").jsonPrimitive.int,
        data.forceGet("ordinal").jsonPrimitive.int,
    )
    gamer.restore(data)
    return gamer
}