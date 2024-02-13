package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObjectTag

data class Factory(
    val typeName: String,
    val level: Int,
) {

    var content: Pair<String, Int>? = null

    fun canBuild(birminghamGamer: BirminghamGamer) {
        // TODO 判定资源是否充足
    }

    fun build(birminghamGamer: BirminghamGamer) {
        // TODO 消耗资源
    }

    fun sell() {
        // TODO 奖励资源
    }
}


var GameObject.factory: Factory
    get() {
        val typeName = this.tags["birmingham:factory_type"]!!.getString()
        val level = this.tags["birmingham:factory_level"]!!.getInt()
        return Factory(typeName, level)
    }
    set(value) {
        this.tags.add(GameObjectTag("birmingham:factory_type", arrayListOf(value.typeName)))
        this.tags.add(GameObjectTag("birmingham:factory_level", arrayListOf(value.level)))
    }

val FACTORY_SET = listOf(
    "brewery" to listOf(2, 2, 2, 1),
    "manufacturer" to listOf(1, 2, 1, 1, 2, 1, 1, 2),
    "cotton_mill" to listOf(3, 2, 3, 3),
    "pottery" to listOf(1, 1, 1, 1, 1),
    "iron_works" to listOf(1, 1, 1, 1),
    "coal_mine" to listOf(1, 2, 2, 2),
)