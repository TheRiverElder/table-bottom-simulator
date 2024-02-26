package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObjectTag
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*

class City(
    val name: String,
    val index: Int,
    val factoryTypeNames: List<String>,
    val placeholderObjectUid: Int,
) : Persistable {
    override fun save(): JsonObject = buildJsonObject {
        put("name", name)
        put("index", index)
        put("factoryTypeNames", factoryTypeNames.save())
        put("placeholderObjectUid", placeholderObjectUid)
    }

    override fun restore(data: JsonObject) {}

    lateinit var group: CityGroup

    var cachedFactory: Factory? = null
}

fun restoreCity(data: JsonObject): City = City(
    data.forceGet("name").jsonPrimitive.content,
    data.forceGet("index").jsonPrimitive.int,
    data.forceGet("factoryTypeNames").jsonArray.map { it.jsonPrimitive.content },
    data.forceGet("placeholderObjectUid").jsonPrimitive.int,
)

var GameObject.city: City?
    get() {
        val tag = tags["birmingham:city"] ?: return null
        return City(tag.getString(0), tag.getInt(1), tag.values.drop(2).mapNotNull { it as? String }, uid)
    }
    set(value) {
        if (value == null) tags.removeByKey("birmingham:city")
        else tags.add(
            GameObjectTag(
                "birmingham:city",
                (listOf(value.name, value.index) + value.factoryTypeNames).toMutableList()
            )
        )
    }