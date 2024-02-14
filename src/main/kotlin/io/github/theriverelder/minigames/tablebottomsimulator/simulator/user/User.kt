package io.github.theriverelder.minigames.tablebottomsimulator.simulator.user

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*

class User(
    val simulator: TableBottomSimulatorServer,
    val uid: Int,
    var name: String = "Anonymous#${uid}",
    var sight: Vector2 = Vector2.zero(),
    var gamerUid: Int? = null,
    var isEditor: Boolean = true,
) : Persistable {
    var destroyed: Boolean = false


    var gamer: Gamer?
        get() {
            return simulator.gamers[gamerUid ?: return null]
        }
        set(value) {
            gamerUid = value?.uid
        }

    fun remove() {
        simulator.users.remove(this)
    }

    override fun save(): JsonObject = buildJsonObject {
        put("uid", uid)
        put("name", name)
        put("sight", sight.save())
        put("gamerUid", gamerUid?.let { JsonPrimitive(it) } ?: JsonNull)
        put("isEditor", isEditor)
        put("destroyed", destroyed)
    }

    override fun restore(data: JsonObject) {
        name = data.forceGet("name").jsonPrimitive.content
        sight = restoreVector2(data.forceGet("sight").jsonObject)
        gamerUid = data.forceGet("gamerUid").jsonPrimitive.int
        isEditor = data.forceGet("isEditor").jsonPrimitive.booleanOrNull ?: false
    }

    override fun toString(): String = "$name#$uid($gamerUid)"
}