package io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject

import io.github.theriverelder.minigames.lib.management.AutoIncrementObservableRegistry
import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel.UpdateGameObjectSelfOptions
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.restoreVector2
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*

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

    fun <T : Behavior<T>> createAndAddBehavior(type: BehaviorType<T>, uid: Int? = null): T {
        val behavior =
            if (uid == null) behaviors.addRaw { type.create(this, it) }
            else {
                val b = type.create(this, uid)
                behaviors.add(b)
                b
            }
        behavior.onInitialize()
        return behavior as T
    }

    fun <T : Behavior<T>> getBehaviorByType(type: BehaviorType<T>): T? =
        behaviors.values.find { it.type == type } as? T

    fun <T : Behavior<T>> getOrCreateAndAddBehaviorByType(type: BehaviorType<T>, uid: Int? = null): T =
        behaviors.values.find { it.type == type } as? T ?: createAndAddBehavior(type, uid)

    fun sendUpdateFull() = simulator.channelGameObject.sendUpdateGameObjectFull(this)

    fun sendUpdateSelf(options: UpdateGameObjectSelfOptions? = null) =
        simulator.channelGameObject.sendUpdateGameObjectSelf(this, options)

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
                behavior = createAndAddBehavior(type, behaviorUid)
            }
            behavior.restore(d)
        }
    }

    fun saveSelf(options: UpdateGameObjectSelfOptions = UpdateGameObjectSelfOptions()): JsonObject = buildJsonObject {
        put("uid", uid)
        if (options.position) put("position", position.save())
        if (options.size) put("size", size.save())
        if (options.rotation) put("rotation", rotation)
        if (options.background) put("background", background)
        if (options.shape) put("shape", shape)
        if (options.tags) put("tags", tags.values.save())
    }

    fun restoreSelf(data: JsonObject) {
        data["position"]?.let { position = restoreVector2(it.jsonObject) }
        data["size"]?.let { size = restoreVector2(it.jsonObject) }
        data["rotation"]?.let { rotation = it.jsonPrimitive.double }
        data["background"]?.let { background = it.jsonPrimitive.content }
        data["shape"]?.let { shape = it.jsonPrimitive.content }
        data["tags"]?.let {
            tags.clear()
            it.jsonArray.forEach { tagData -> tags.add(restoreGameObjectTag(tagData.jsonObject)) }
        }
    }

    override fun hashCode(): Int = uid
}

fun restoreGameObject(data: JsonObject, simulator: TableBottomSimulatorServer): GameObject {
    val gameObject = GameObject(simulator, data.forceGet("uid").jsonPrimitive.int)
    gameObject.restore(data)
    return gameObject
}