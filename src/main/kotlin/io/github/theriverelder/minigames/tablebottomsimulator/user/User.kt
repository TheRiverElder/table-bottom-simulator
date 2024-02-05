package io.github.theriverelder.minigames.tablebottomsimulator.user

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*

class User(
    val simulator: TableBottomSimulatorServer,
    val uid: Int,
    var name: String,
    var sight: Vector2 = Vector2.zero(),
    var color: String = "white",
    var isEditor: Boolean = true,
    var cardObjectUidList: List<Int> = emptyList(),
) : Persistable {
    var destroyed: Boolean = false

    fun remove() {
        simulator.users.remove(this)
    }

    override fun save(): JsonObject = buildJsonObject {
        put("uid", JsonPrimitive(uid))
        put("name", JsonPrimitive(name))
        put("sight", sight.save())
        put("color", JsonPrimitive(color))
        put("isEditor", JsonPrimitive(isEditor))
        put("cardObjectUidList", buildJsonArray { cardObjectUidList.forEach { add(it) } })
        put("destroyed", JsonPrimitive(destroyed))
    }

    override fun restore(data: JsonObject) {
        name = data["name"]?.jsonPrimitive?.content!!
        sight = restoreVector2(data["sight"]?.jsonObject!!)
        color = data["color"]!!.jsonPrimitive.content
        isEditor = data["isEditor"]?.jsonPrimitive?.booleanOrNull ?: false
        cardObjectUidList = data["cardObjectUidList"]?.jsonArray?.map { it.jsonPrimitive.int } ?: emptyList()
    }

    val cards: List<Card> get() = cardObjects.map { it.getBehaviorByType(CardBehavior.TYPE)!!.card!! }

    val cardObjects: List<GameObject> get() = cardObjectUidList.map { simulator.gameObjects[it]!! }

    override fun toString(): String = "$name#$uid($color)"
}