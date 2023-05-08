package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*

class ControlChannel(name: String, simulator: TableBottomSimulatorServer) : Channel(name, simulator) {
    override fun onReceive(data: JsonObject, sender: User) {
        val uid = data["uid"]!!.jsonPrimitive.int
        val gameObject = simulator.gameObjects[uid] ?: return
        gameObject.position = restoreVector2(data["position"]!!.jsonObject)
        gameObject.size = restoreVector2(data["size"]!!.jsonObject)
        gameObject.rotation = data["rotation"]!!.jsonPrimitive.double
        gameObject.background = data["background"]?.jsonPrimitive?.content ?: gameObject.background
        gameObject.shape = data["shape"]?.jsonPrimitive?.content ?: gameObject.shape
        runBlocking {
            simulator.channelIncrementalUpdate.broadcastGameObjectUpdate(gameObject)
        }
    }
}