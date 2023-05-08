package io.github.theriverelder.minigames.tablebottomsimulator

import kotlinx.serialization.json.JsonObject

interface Persistable {
    fun save(): JsonObject
    fun restore(data: JsonObject)
}