package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2OrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.lang.Exception

class EditChannel(name: String, simulator: TableBottomSimulatorServer) : Channel(name, simulator) {

    override fun onReceive(data: JsonObject, sender: User) {
        val action = data["action"]?.jsonPrimitive?.content ?: throw Exception("No action")
        when (action) {
            "createEmptyGameObject" -> {
                val position = restoreVector2OrNull(data["position"]?.jsonObject) ?: Vector2.zero()
                val gameObject = simulator.createAndAddGameObject()
                gameObject.position = position
                simulator.channelIncrementalUpdate.broadcastGameObjectUpdate(gameObject)
            }
            "pasteGameObject" -> {
                val position = restoreVector2OrNull(data["position"]?.jsonObject)
                val gameObjectData = data["data"]?.jsonObject ?: throw Exception("No data")
                val gameObjectUid = gameObjectData["uid"]?.jsonPrimitive?.int
                val gameObject =
                    if (gameObjectUid != null && !simulator.gameObjects.containsKey(gameObjectUid)) simulator.createAndAddGameObject(gameObjectUid)
                    else simulator.createAndAddGameObject()
                gameObject.restore(gameObjectData)
                if (position != null) {
                    gameObject.position = position
                }
                simulator.channelIncrementalUpdate.broadcastGameObjectUpdate(gameObject)
            }
            "removeGameObject" -> {
                val uid = data["uid"]?.jsonPrimitive?.int ?: throw Exception("No uid")
                val gameObject = simulator.gameObjects[uid]
                if (gameObject != null) {
                    gameObject.remove()
                    simulator.channelIncrementalUpdate.broadcastGameObjectUpdate(gameObject)
                }
            }
        }

    }


}