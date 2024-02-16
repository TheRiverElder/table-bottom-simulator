package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionGuide
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.Factory
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.factory
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
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

    override fun restore(data: JsonObject) {
        money = data.forceGet("money").jsonPrimitive.int
    }

    // 只记录剩下没打出的工厂
    val factoryObjectUidStacks = HashMap<String, List<Int>>()

    fun initialize() {
        // 初始化玩家的工厂指示物
        // 找不到规则书，就先这么写代替一下罢

        val gamer = gamer!!

        val cardAreaAnchor = gamer.home

        gamer.cardObjects.forEachIndexed { index, gameObject ->
            gameObject.position = cardAreaAnchor + Vector2(index * 550, 0)
            gameObject.rotation = 0.0
            gameObject.sendUpdateSelf()
        }

        val factoryPreparingAreaAnchor = gamer.home + Vector2(0, 800)

        factoryObjectUidStacks.clear()
        FACTORY_SET.forEachIndexed { typeIndex, pair ->
            val typeName = pair.first
            val levels = pair.second

            val factoryList = levels.flatMapIndexed { level, amountOfLevel ->
                buildList<Int>(amountOfLevel) {
                    val obj = game.simulator.createAndAddGameObject()
                    obj.factory = Factory(gamerUid, typeName, level, Factory.STATUS_READY)
                    val card = game.extension.cardSeriesFactory.cards["${gamer.color}_${typeName}_level_${
                        (level).toString().padStart(2, '0')
                    }"]!!
                    obj.card = card
                    obj.position = factoryPreparingAreaAnchor + Vector2(level * 250, typeIndex * 250)
                    obj.size = Vector2(238, 238)
                    obj.shape = "rectangle"

                    obj.sendUpdateFull()

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
