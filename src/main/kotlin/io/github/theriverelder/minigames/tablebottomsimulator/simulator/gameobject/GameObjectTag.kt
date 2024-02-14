package io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject

import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*

class GameObjectTag(
    val name: String,
    var values: MutableList<Any> = ArrayList(),
) : Persistable {

    var value: Any
        get() = values[0]
        set(value) { values[0] = value }

    fun getInt(index: Int = 0): Int {
        val raw = values[index]
        if (raw is Int) return raw
        if (raw is String) return raw.toInt()
        throw NumberFormatException("value[$index] is not a number: $raw")
    }

    fun getString(index: Int = 0): String {
        val raw = values[index]
        return raw.toString()
    }

    override fun save(): JsonObject {
        return buildJsonObject {
            put("name", name)
            put("values", values.save())
        }
    }

    override fun restore(data: JsonObject) {
        values = ArrayList(data["values"]?.jsonArray?.map { with(it.jsonPrimitive) {
            intOrNull ?: booleanOrNull ?: contentOrNull ?: JsonNull
        } } ?: emptyList())
    }
}

fun restoreGameObjectTag(data: JsonObject): GameObjectTag {
    val name: String = data["name"]!!.jsonPrimitive.content
    val tag = GameObjectTag(name)
    tag.restore(data)
    return tag
}