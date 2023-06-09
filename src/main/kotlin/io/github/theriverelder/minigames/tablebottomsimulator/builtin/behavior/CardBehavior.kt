package io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior

import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.lib.util.addAll
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorAdaptor
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.Side
import kotlinx.serialization.json.*

class CardBehavior(type: BehaviorType<CardBehavior>, host: GameObject, uid: Int) :
    BehaviorAdaptor<CardBehavior>(type, host, uid) {

    var flipped: Boolean = false
    var series: CardSeries? = null
    var card: Card? = null

    override fun onInitialize() {
        this.refreshHost()
    }

    fun refreshHost() {
        val card = card
        if (card != null) {
            this.host.background = if (!flipped) card.face else card.series.back
            this.host.sendUpdateSelf()
        }
    }

    override fun save() = buildJsonObject {
        addAll(super.save())
        put("flipped", JsonPrimitive(flipped))
        put("series", JsonPrimitive(series?.name))
        put("card", JsonPrimitive(card?.name))
    }

    override fun restore(data: JsonObject) {
        super.restore(data);
        this.flipped = data.forceGet("flipped").jsonPrimitive.boolean
        this.series = CardSeries.SERIES[data.forceGet("series").jsonPrimitive.content]
        this.card = this.series?.cards?.get(data.forceGet("card").jsonPrimitive.content)
        this.refreshHost()
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