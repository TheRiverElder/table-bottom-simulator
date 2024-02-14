package io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject

import io.github.theriverelder.minigames.lib.management.AutoIncrementObservableRegistry
import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2
import kotlinx.serialization.json.*
import java.lang.Exception

open class GameObject(
    val simulator: TableBottomSimulatorServer,
    val uid: Int,
) : Persistable {

    var position: Vector2 = Vector2.zero()
    var size: Vector2 = Vector2.zero()
    var rotation: Double = 0.0
    var background: String = ""
    var shape: String = "circle"
    val tags = Registry(GameObjectTag::name)

    val behaviors = AutoIncrementObservableRegistry(Behavior<*>::uid)

    fun remove() {
        simulator.gameObjects.remove(this)
        behaviors.values.forEach { it.onDestroy() }
    }

    fun <T : Behavior<T>> createAndAddBehavior(type: BehaviorType<T>): T {
        val behavior = behaviors.addRaw { type.create(this, it) }
        behavior.onInitialize()
        return behavior as T
    }

    fun <T : Behavior<T>> getBehaviorByType(type: BehaviorType<T>): T? =
        behaviors.values.find { it.type == type } as? T

    fun <T : Behavior<T>> getOrCreateAndAddBehaviorByType(type: BehaviorType<T>): T =
        behaviors.values.find { it.type == type } as? T ?: createAndAddBehavior(type)

    fun sendUpdateFull() = simulator.channelGameObject.sendUpdateGameObjectFull(this)

    fun sendUpdateSelf() = simulator.channelGameObject.sendUpdateGameObjectSelf(this)

    override fun save(): JsonObject = buildJsonObject {
        saveSelf().entries.forEach { put(it.key, it.value) }
        put("behaviors", buildJsonArray { behaviors.values.forEach { add(it.save()) } })
    }

    override fun restore(data: JsonObject) {
        restoreSelf(data)
        for (behaviorData in (data["behaviors"] ?: throw Exception("No field: behaviors")).jsonArray) {
            val d = behaviorData.jsonObject
            val behaviorTypeName = (d["type"] ?: throw Exception("No field: behavior[x].type")).jsonPrimitive.content
            val behaviorUid = (d["uid"] ?: throw Exception("No field: behavior[x].uid")).jsonPrimitive.int
            var behavior = behaviors[behaviorUid]
            if (behavior == null) {
                val type =
                    simulator.behaviorTypes[behaviorTypeName] ?: throw Exception("No behavior type: $behaviorTypeName")
                behavior = createAndAddBehavior(type)
            }
            behavior.restore(d)
            behaviors += behavior
        }
    }

    fun saveSelf(): JsonObject = buildJsonObject {
        put("uid", uid)
        put("position", position.save())
        put("size", size.save())
        put("rotation", rotation)
        put("background", background)
        put("shape", shape)
        put("tags", tags.values.save())
    }

    fun restoreSelf(data: JsonObject) {
        position = restoreVector2((data["position"] ?: throw Exception("No field: position")).jsonObject)
        size = restoreVector2((data["size"] ?: throw Exception("No field: size")).jsonObject)
        rotation = (data["rotation"] ?: throw Exception("No field: rotation")).jsonPrimitive.double
        background = (data["background"] ?: throw Exception("No field: background")).jsonPrimitive.content
        shape = (data["shape"] ?: throw Exception("No field: shape")).jsonPrimitive.content

        tags.clear()
        (data["tags"] ?: throw Exception("No field: tags")).jsonArray.forEach {
            val tagData = it.jsonObject
            val name = tagData.forceGet("name").jsonPrimitive.content
            val tag = GameObjectTag(name)
            tag.restore(tagData)
            tags.add(tag)
        }
    }

    override fun hashCode(): Int = uid
}

fun restoreGameObject(data: JsonObject, simulator: TableBottomSimulatorServer): GameObject {
    val gameObject = GameObject(simulator, data.forceGet("uid").jsonPrimitive.int)
    gameObject.restore(data)
    return gameObject
}