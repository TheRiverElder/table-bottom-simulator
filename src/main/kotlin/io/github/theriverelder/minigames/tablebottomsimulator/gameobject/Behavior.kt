package io.github.theriverelder.minigames.tablebottomsimulator.gameobject

import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

abstract class  Behavior<T : Behavior<T>>(
    val type: BehaviorType<T>,
    val host: GameObject,
    val uid: Int,
) : Persistable {

    val simulator: TableBottomSimulatorServer
        get() = host.simulator

    init {
        onInitialize()
    }

    fun remove() {
        host.behaviors.remove(this)
        this.onDestroy()
    }

    override fun save(): JsonObject = buildJsonObject {
        put("type", JsonPrimitive(type.name))
        put("uid", JsonPrimitive(uid))
        saveData()
    }

    override fun restore(data: JsonObject) {
        restoreData(data)
    }

    abstract fun JsonObjectBuilder.saveData()
    abstract fun restoreData(data: JsonObject)

    abstract fun onInitialize()
    abstract fun onDestroy()

}