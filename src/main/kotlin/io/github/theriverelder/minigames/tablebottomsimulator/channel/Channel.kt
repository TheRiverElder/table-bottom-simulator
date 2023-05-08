package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.tablebottomsimulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

abstract class Channel(
    val name: String,
    val simulator: TableBottomSimulatorServer,
) {

    abstract fun onReceive(data: JsonObject, sender: User)

    suspend fun send(data: JsonObject, receiver: User) {
        val pack = buildJsonObject {
            put("channel", JsonPrimitive(name))
            put("data", data)
        }
        simulator.communication?.sendRawData(Json.encodeToString(pack), receiver)
    }

    suspend fun send(data: JsonObject, receivers: List<User> = emptyList()) {
        receivers.forEach { send(data, it) }
    }

    suspend fun broadcast(data: JsonObject, excepts: Set<User> = emptySet()) {
        send(data, simulator.users.values.filter { it !in excepts }.toList())
    }
}