package io.github.theriverelder.minigames.lib.util

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder

fun JsonObject.forceGet(key: String): JsonElement = this[key] ?: throw Exception("No filed: $key")

fun JsonObjectBuilder.addAll(elements: Map<String, JsonElement>) = elements.entries.forEach { put(it.key, it.value) }