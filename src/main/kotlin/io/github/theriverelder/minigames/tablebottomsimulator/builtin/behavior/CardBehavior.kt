package io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior

import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.lib.util.addAll
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorAdaptor
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.Side
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive

class CardBehavior(type: BehaviorType<CardBehavior>, host: GameObject, uid: Int) :
    BehaviorAdaptor<CardBehavior>(type, host, uid) {

    var series: CardSeries? = null;
    var card: Card? = null;

    override fun onInitialize() {
        this.refreshhost()
    }

    fun refreshhost() {
        val card = card
        if (card != null) {
            this.host.background = card.face
            this.host.sendUpdateSelf()
        }
    }

    override fun save() = buildJsonObject {
        addAll(super.save())
        put("series", JsonPrimitive(series?.name))
        put("card", JsonPrimitive(card?.name))
    }

    override fun restore(data: JsonObject) {
        super.restore(data);
        this.series = CardSeries.SERIES[data.forceGet("series").jsonPrimitive.content]
        this.card = this.series?.cards?.get(data.forceGet("card").jsonPrimitive.content)
        this.refreshhost()
    }

    companion object {
        val TYPE = BehaviorType("card", Side.BOTH, ::CardBehavior)
    }
}

class CardSeries(
    val name: String,
    val back: String,
) {

    val cards = Registry(Card::name);

    companion object {
        val SERIES = Registry(CardSeries::name)
    }

}

class Card(
    val name: String,
    val series: CardSeries,
    val face: String,
)