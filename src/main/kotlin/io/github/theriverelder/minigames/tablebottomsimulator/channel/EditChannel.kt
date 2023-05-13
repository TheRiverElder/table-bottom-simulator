package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.ControllerBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2OrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.lang.Exception

class EditChannel(name: String, simulator: TableBottomSimulatorServer) : Channel(name, simulator) {

    companion object {
        const val CREATE_EMPTY_GAME_OBJECT = "create_empty_game_object"
        const val PASTE_GAME_OBJECT = "paste_game_object"
        const val CREATE_BEHAVIOR = "create_behavior"
    }

    override fun receive(data: JsonObject, sender: User) {
        if (!sender.isEditor) throw Exception("Not editor: $sender")
        when (data.forceGet("action").jsonPrimitive.content) {
            CREATE_EMPTY_GAME_OBJECT -> receiveCreateEmptyGameObject(data)
            PASTE_GAME_OBJECT -> receivePasteGameObject(data)
            CREATE_BEHAVIOR -> receiveCreateBehavior(data)
        }
    }

    private fun receiveCreateEmptyGameObject(data: JsonObject) {
        val position = restoreVector2OrNull(data["position"]?.jsonObject) ?: Vector2.zero()
        val gameObject = simulator.createAndAddGameObject()
        gameObject.position = position
        gameObject.size = Vector2(100.0, 100.0)
        gameObject.createAndAddBehavior(ControllerBehavior.TYPE)

        simulator.channelIncrementalUpdate.sendUpdateGameObjectFull(gameObject)
    }

    private fun receivePasteGameObject(data: JsonObject) {
        val position = restoreVector2OrNull(data["position"]?.jsonObject)
        val gameObjectData = data["data"]?.jsonObject ?: throw Exception("No data")
        val gameObjectUid = gameObjectData["uid"]?.jsonPrimitive?.int
        val gameObject =
            if (gameObjectUid != null && !simulator.gameObjects.containsKey(gameObjectUid)) simulator.createAndAddGameObject(gameObjectUid)
            else simulator.createAndAddGameObject()
        gameObject.restore(gameObjectData)
        if (position != null) gameObject.position = position

        simulator.channelIncrementalUpdate.sendUpdateGameObjectFull(gameObject)
    }

    private fun receiveCreateBehavior(data: JsonObject) {
        val uid = data["uid"]?.jsonPrimitive?.int ?: throw Exception("No uid")
        val gameObject = simulator.gameObjects[uid] ?: throw Exception("No game object with uid: $uid")
        val typeName = data["type"]?.jsonPrimitive?.content ?: throw Exception("No type")
        val type = simulator.behaviorTypes[typeName] ?: throw Exception("No behavior type: $typeName")
        val behavior = gameObject.createAndAddBehavior(type)

        simulator.channelIncrementalUpdate.sendUpdateBehavior(behavior)
    }


}