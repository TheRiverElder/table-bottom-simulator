package io.github.theriverelder.minigames.tablebottomsimulator.util

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.serialization.json.*


fun Vector2.save() = buildJsonObject {
    put("x", JsonPrimitive(x))
    put("y", JsonPrimitive(y))
}

fun restoreVector2(data: JsonObject): Vector2 =
    Vector2(data["x"]?.jsonPrimitive?.double ?: 0.0, data["y"]?.jsonPrimitive?.double ?: 0.0)

fun restoreVector2OrNull(data: JsonObject?): Vector2? {
    if (data == null) return null
    return Vector2(
        data["x"]?.jsonPrimitive?.double ?: 0.0,
        data["y"]?.jsonPrimitive?.double ?: 0.0,
    )
}
