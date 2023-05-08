package io.github.theriverelder.minigames.tablebottomsimulator.user

import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive

class Gamer(
    val name: String,
    val color: String,
) : Persistable {
    var user: User? = null

    override fun save(): JsonObject = buildJsonObject {
        put("name", JsonPrimitive(name))
        put("color", JsonPrimitive(color))
    }

    override fun restore(data: JsonObject) { }
}

fun restoreGamer(data: JsonObject): Gamer =
    Gamer(data["name"]!!.jsonPrimitive.content, data["color"]!!.jsonPrimitive.content)