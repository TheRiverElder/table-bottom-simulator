package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObjectTag
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.json.*

class Network(
    val cityNames: List<String>,
    val periods: List<Int>,
    val placeholderObjectUid: Int,
) : Persistable, GraphNode<Group, BirminghamGraphNodeContext> {
    override fun save(): JsonObject = buildJsonObject {
        put("cityNames", cityNames.save())
        put("periods", periods.save())
        put("placeholderObjectUid", placeholderObjectUid)
    }

    override fun restore(data: JsonObject) { }

    override var neighbors: List<Group> = emptyList()

    override fun canPass(context: BirminghamGraphNodeContext): Boolean {
        TODO("Not yet implemented")
    }

    var cachedWay: Way? = null

}

fun restoreNetwork(data: JsonObject): Network = Network(
    data.forceGet("cityNames").jsonArray.map { it.jsonPrimitive.content },
    data.forceGet("periods").jsonArray.map { it.jsonPrimitive.int },
    data.forceGet("placeholderObjectUid").jsonPrimitive.int,
)

var GameObject.network: Network?
    get() {
        val tag = tags["birmingham:network"] ?: return null
        val periodsString = tag.getString(0) ?: ""
        val periods = buildList(2) {
            if (periodsString.contains('c')) add(1)
            if (periodsString.contains('r')) add(2)
        }
        val cityNames = tag.values?.drop(1)?.mapNotNull { it as? String }?.filter { it.isNotBlank() } ?: emptyList()
        return Network(cityNames, periods, uid)
    }
    set(value) {
        if (value == null) {
            tags.removeByKey("birmingham:network")
            return
        }
        var periodString = ""
        for (period in value.periods) {
            periodString += when (period) {
                1 -> "c"
                2 -> "r"
                else -> ""
            }
        }
        tags.add(GameObjectTag("birmingham:network", (listOf(periodString) + value.cityNames).toMutableList()))
    }