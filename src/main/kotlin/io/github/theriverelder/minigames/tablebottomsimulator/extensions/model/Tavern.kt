package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObjectTag
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*

class Tavern(
    val factoryTypeNames: List<String>,
    val gameObjectUid: Int,
) : Persistable {
    override fun save(): JsonObject = buildJsonObject {
        put("factoryTypeNames", factoryTypeNames.save())
        put("gameObjectUid", gameObjectUid)
    }

    override fun restore(data: JsonObject) {}

    lateinit var group: Group<Market, Network>

    var cachedFactory: Factory? = null
}

fun restoreTavern(data: JsonObject): Tavern = Tavern(
    data.forceGet("factoryTypeNames").jsonArray.map { it.jsonPrimitive.content },
    data.forceGet("gameObjectUid").jsonPrimitive.int,
)

var GameObject.tavern: Tavern?
    get() {
        val tag = tags["birmingham:tavern"] ?: return null
        return Tavern(tag.values.mapNotNull { it as? String }, uid)
    }
    set(value) {
        if (value == null) tags.removeByKey("birmingham:tavern")
        else tags.add(
            GameObjectTag(
                "birmingham:tavern",
                value.factoryTypeNames.toMutableList(),
            )
        )
    }