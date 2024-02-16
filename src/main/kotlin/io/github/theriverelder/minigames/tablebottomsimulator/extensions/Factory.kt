package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObjectTag

data class Factory(
    val ownerGamerUid: Int,
    val typeName: String,
    val level: Int,
    val status: String,
) {

    companion object {
        val STATUS_READY = "status_ready"
        val STATUS_BUILT = "status_built"
        val STATUS_SOLD = "status_sold"
    }

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
        val tag = tags["birmingham:factory"]!!
        val ownerGamerUid = tag.getInt(0)
        val typeName = tag.getString(1)
        val level = tag.getInt(2)
        val status = tag.getString(3)
        return Factory(ownerGamerUid, typeName, level, status)
    }
    set(value) {
        tags.add(
            GameObjectTag(
                "birmingham:factory_owner", arrayListOf(
                    value.ownerGamerUid,
                    value.typeName,
                    value.level,
                    value.status,
                )
            )
        )
    }