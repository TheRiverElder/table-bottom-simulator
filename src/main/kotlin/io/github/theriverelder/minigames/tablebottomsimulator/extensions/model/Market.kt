package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObjectTag
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import kotlinx.serialization.json.*

class Market(
    val name: String,
    val index: Int,
    val placeholderObjectUid: Int,
) : Persistable {
    override fun save(): JsonObject = buildJsonObject {
        put("name", name)
        put("index", index)
        put("placeholderObjectUid", placeholderObjectUid)
    }

    override fun restore(data: JsonObject) {}

    lateinit var group: Group<Market, Network>

    var cachedTavern: Tavern? = null
}

fun restoreMarket(data: JsonObject): Market = Market(
    data.forceGet("name").jsonPrimitive.content,
    data.forceGet("index").jsonPrimitive.int,
    data.forceGet("placeholderObjectUid").jsonPrimitive.int,
)

var GameObject.market: Market?
    get() {
        val tag = tags["birmingham:market"] ?: return null
        return Market(tag.getString(0), tag.getInt(1), uid)
    }
    set(value) {
        if (value == null) tags.removeByKey("birmingham:market")
        else tags.add(
            GameObjectTag(
                "birmingham:market",
                mutableListOf(value.name, value.index),
            )
        )
    }