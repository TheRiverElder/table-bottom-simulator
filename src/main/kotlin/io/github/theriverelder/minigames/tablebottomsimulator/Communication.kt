package io.github.theriverelder.minigames.tablebottomsimulator

import io.github.theriverelder.minigames.tablebottomsimulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.lang.Exception

class Communication(
    val simulator: TableBottomSimulatorServer,
    val rawDataSender: suspend (String, User) -> Unit,
) {


    suspend fun sendRawData(rawData: String, receiver: User) = rawDataSender(rawData, receiver)

    fun receiveRawData(rawData: String, sender: User) {
        val pack = Json.decodeFromString<JsonObject>(rawData)
        val channelName = (pack["channel"] ?: throw Exception("No filed: channel")).jsonPrimitive.content
        val channelData = (pack["data"] ?: throw Exception("No filed: data")).jsonObject
        val channel = simulator.channels[channelName] ?: throw Exception("No such channel: $channelName")
        channel.onReceive(channelData, sender)
    }


}