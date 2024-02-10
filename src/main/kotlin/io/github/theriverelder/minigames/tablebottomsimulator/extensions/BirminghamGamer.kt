package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.ActionGuide
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObjectTag
import io.github.theriverelder.minigames.tablebottomsimulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.json.*
import java.util.concurrent.atomic.AtomicInteger

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

    val factoryObjectUidStacks = HashMap<String, List<Int>>()

    fun initialize() {
        // 初始化玩家的工厂指示物
        // 找不到规则书，就先这么写代替一下罢
        factoryObjectUidStacks.clear()
        FACTORY_SET.forEach { pair ->
            val typeName = pair.first
            val levels = pair.second

            val factoryList = levels.flatMapIndexed { level, amountOfLevel ->
                buildList<Int>(amountOfLevel) {
                    val obj = game.simulator.createAndAddGameObject()
                    obj.factory = Factory(typeName, level)
                    // TODO obj.card = card
                    obj.uid
                }
            }
            factoryObjectUidStacks[typeName] = factoryList
        }
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
