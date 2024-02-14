package io.github.theriverelder.minigames.tablebottomsimulator.util

import kotlinx.serialization.json.*

interface Persistable {
    fun save(): JsonObject
    fun restore(data: JsonObject)
}



fun Any.save(): JsonElement = when (this) {
    is String -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is Persistable -> this.save()
    is Iterable<*> -> buildJsonArray { forEach { add(it?.save() ?: JsonNull) } }
    is Map<*, *> -> buildJsonObject { forEach { k, v -> put(k.toString(), v?.save() ?: JsonNull) } }
    else -> JsonNull
}