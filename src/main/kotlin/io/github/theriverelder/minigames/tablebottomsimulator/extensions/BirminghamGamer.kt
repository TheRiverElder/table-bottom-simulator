package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel.UpdateGameObjectSelfOptions
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionGuide
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.*
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*

class BirminghamGamer(
    val game: BirminghamGame,
    val gamerUid: Int,
    val ordinal: Int,
    var money: Int = 0,
    incomePoints: Int = 10,
) : Persistable {

    val incomeTrack: IncomeTrack = IncomeTrack(points = incomePoints)

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
        put("incomeTrackPoints", incomeTrack.points)
        put("factoryObjectUidStacks", factoryObjectUidStacks.save())
    }

    fun extractData(): JsonObject = buildJsonObject {
        put("gamerUid", gamer?.uid)
        put("ordinal", ordinal)
        put("money", money)
        put("incomeTrackPoints", incomeTrack.points)
        put("incomeTrackLevel", incomeTrack.level)
        put("factoryObjectUidStacks", factoryObjectUidStacks.save())
    }

    override fun restore(data: JsonObject) {
        money = data.forceGet("money").jsonPrimitive.int
        incomeTrack.points = data.forceGet("incomeTrackPoints").jsonPrimitive.int
        factoryObjectUidStacks.clear()
        data.forceGet("factoryObjectUidStacks").jsonObject.forEach { k, v ->
            factoryObjectUidStacks[k] = v.jsonArray.map { it.jsonPrimitive.int }
        }
    }

    // 只记录剩下没打出的工厂
    val factoryObjectUidStacks = HashMap<String, List<Int>>()

    fun initialize() {
        // 初始化玩家的工厂指示物
        // 找不到规则书，就先这么写代替一下罢

        val gamer = gamer!!

        gamer.cleanupCards(50.0)
            .forEach {
                it.rotation = 0.0
                it.sendUpdateSelf(UpdateGameObjectSelfOptions(position = true, rotation = true))
            }

        val factoryPreparingAreaAnchor = gamer.home + Vector2(0, 800)

        factoryObjectUidStacks.clear()
        FACTORY_SET.forEachIndexed { typeIndex, pair ->
            val typeName = pair.first
            val levels = pair.second

            val factoryList = levels.flatMapIndexed { level, amountOfLevel ->
                buildList(amountOfLevel) {
                    repeat(amountOfLevel) { indexOfSameLevel ->
                        val obj = game.simulator.createAndAddGameObject()
                        obj.factory = Factory(gamerUid, typeName, level, Factory.STATUS_READY, obj.uid)
                        val cardName = "${gamer.color}_${typeName}_level_${(level).toString().padStart(2, '0')}"
                        val card = game.extension.cardSeriesFactory.cards[cardName]!!
                        obj.card = card
                        val offsetIndex = amountOfLevel - indexOfSameLevel - 1
                        obj.position = factoryPreparingAreaAnchor +
                                Vector2(level * 300, typeIndex * 350) +
                                Vector2((amountOfLevel - offsetIndex) * 20, (amountOfLevel - offsetIndex) * 20)
                        obj.shape = "rectangle"

                        obj.sendUpdateFull()

                        add(obj.uid)
                    }
                }.reversed()
            }
            factoryObjectUidStacks[typeName] = factoryList
        }
    }

    fun createWayObject(period: Int = game.period): GameObject {
        val networkName = when (period) {
            1 -> "canal"
            2 -> "rail"
            else -> "error"
        }
        val gamer = gamer!!
        val gameObject = game.simulator.createAndAddGameObject()
        val card = game.extension.cardSeriesWay.cards["${gamer.color}_${networkName}"]!!
        gameObject.card = card
        gameObject.way = Way(period, gamerUid, gameObject.uid)
        gameObject.sendUpdateFull()
        return gameObject
    }

    // 只进行GameObject的操作，不检查条件
    fun build(factory: Factory, city: City) {
        factory.ownerGamerUid
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
