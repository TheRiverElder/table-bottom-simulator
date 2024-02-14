package io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.Behavior
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.serialization.json.*

class GameObjectChannel(simulator: TableBottomSimulatorServer) : Channel("game_object", simulator) {

    companion object {
        const val UPDATE_GAME_OBJECT_FULL = "update_game_object_full"
        const val UPDATE_GAME_OBJECT_SELF = "update_game_object_self"
        const val REMOVE_GAME_OBJECT = "remove_game_object"
        const val UPDATE_BEHAVIOR = "update_behavior"
        const val REMOVE_BEHAVIOR = "remove_behavior"
    }


    // 广播，发送GameObject的几何数据以及Behavior数据，接收者若无对应UID的GameObject则创建
    fun sendUpdateGameObjectFull(obj: GameObject) = broadcast(buildJsonObject {
        put("action", UPDATE_GAME_OBJECT_FULL)
        put("gameObject", obj.save())
    })

    // 广播，只发送GameObject的几何数据，不包括Behavior数据，接收者若无对应UID的GameObject也不创建
    fun sendUpdateGameObjectSelf(obj: GameObject) = broadcast(buildJsonObject {
        put("action", UPDATE_GAME_OBJECT_SELF)
        put("gameObject", obj.saveSelf())
    })

    // 广播，移除GameObject
    fun sendRemoveGameObject(obj: GameObject) = broadcast(buildJsonObject {
        put("action", REMOVE_GAME_OBJECT)
        put("uid", obj.uid)
    })

    // 广播，发送Behavior数据，接收者若无对应UID的Behavior则创建
    fun sendUpdateBehavior(behavior: Behavior<*>) = broadcast(buildJsonObject {
        put("action", UPDATE_BEHAVIOR)
        put("hostUid", behavior.host.uid)
        put("behavior", behavior.save())
    })

    // 广播，移除Behavior
    fun sendRemoveBehavior(behavior: Behavior<*>) = broadcast(buildJsonObject {
        put("action", REMOVE_BEHAVIOR)
        put("hostUid", behavior.host.uid)
        put("behaviorUid", behavior.uid)
    })


    override fun receive(data: JsonObject, sender: User) {
        if (!sender.isEditor) throw Exception("Not editor: $sender")
        val action = data["action"]?.jsonPrimitive?.content ?: throw Exception("No field: action")
        when(action) {
            UPDATE_GAME_OBJECT_FULL -> receiveUpdateGameObjectFull(data)
            UPDATE_GAME_OBJECT_SELF -> receiveUpdateGameObjectSelf(data)
            REMOVE_GAME_OBJECT -> receiveRemoveGameObject(data)
            UPDATE_BEHAVIOR -> receiveUpdateBehavior(data)
            REMOVE_BEHAVIOR -> receiveRemoveBehavior(data)
            else -> throw Exception("Unknown action: $action")
        }
    }

    private fun receiveUpdateGameObjectFull(data: JsonObject) {
        val gameObjectData = data.forceGet("gameObject").jsonObject
        val gameObject = simulator.gameObjects.getOrThrow(gameObjectData.forceGet("uid").jsonPrimitive.int)
        gameObject.restore(gameObjectData)

        sendUpdateGameObjectFull(gameObject)
    }

    private fun receiveUpdateGameObjectSelf(data: JsonObject) {
        val gameObjectData = data.forceGet("gameObject").jsonObject
        val gameObject = simulator.gameObjects.getOrThrow(gameObjectData.forceGet("uid").jsonPrimitive.int)
        gameObject.restoreSelf(gameObjectData)

        sendUpdateGameObjectSelf(gameObject)
    }

    private fun receiveRemoveGameObject(data: JsonObject) {
        val gameObjectUid = data.forceGet("uid").jsonPrimitive.int
        val gameObject = simulator.gameObjects[gameObjectUid]
        if (gameObject != null) {
            gameObject.remove()
            sendRemoveGameObject(gameObject)
        }
    }

    private fun receiveUpdateBehavior(data: JsonObject) {
        val host = simulator.gameObjects.getOrThrow(data.forceGet("hostUid").jsonPrimitive.int)
        val behaviorData = data.forceGet("behavior").jsonObject
        val behavior = host.behaviors.getOrThrow(behaviorData.forceGet("uid").jsonPrimitive.int)
        behavior.restore(behaviorData)

        sendUpdateBehavior(behavior)
    }

    private fun receiveRemoveBehavior(data: JsonObject) {
        val host = simulator.gameObjects.getOrThrow(data.forceGet("hostUid").jsonPrimitive.int)
        val behavior = host.behaviors[data.forceGet("behaviorUid").jsonPrimitive.int]
        if (behavior != null) {
            behavior.remove()
            sendRemoveBehavior(behavior)
        }
    }
}