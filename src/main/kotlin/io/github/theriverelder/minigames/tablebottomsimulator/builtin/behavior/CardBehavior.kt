package io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior

import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.lib.util.addAll
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorAdaptor
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.Side
import io.github.theriverelder.minigames.tablebottomsimulator.save
import kotlinx.serialization.json.*

class CardBehavior(type: BehaviorType<CardBehavior>, host: GameObject, uid: Int) :
    BehaviorAdaptor<CardBehavior>(type, host, uid) {

    var flipped: Boolean = false
    var series: CardSeries? = null
    var card: Card? = null
        set(value) {
            if (series == null && value != null) series = value.series
            field = value
            refreshHost()
        }

    override fun onInitialize() {
        this.refreshHost()
    }

    fun refreshHost() {
        val card = card
        if (card != null) {
            this.host.background = if (!flipped) card.face else card.back
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
) : Persistable {

    val cards = Registry(Card::name);

    companion object {
        val SERIES = Registry(CardSeries::name)
    }

    override fun save(): JsonObject = buildJsonObject {
        put("name", name)
        put("back", back)
        put("cards", cards.values.save())
    }

    override fun restore(data: JsonObject) {
        cards.clear()
        data.forceGet("cards").jsonArray.forEach { cards.add(restoreCard(it.jsonObject, this)) }
    }

}

fun restoreCardSeries(data: JsonObject): CardSeries {
    val series = CardSeries(
        data.forceGet("name").jsonPrimitive.content,
        data.forceGet("back").jsonPrimitive.contentOrNull ?: "",
    )
    series.restore(data)
    return series
}

data class Card (
    val name: String,
    val series: CardSeries,
    val face: String,
    val cardBack: String? = null, // 为null则代表用series.back
) : Persistable {
    override fun save(): JsonObject = buildJsonObject {
        put("name", name)
        put("seriesName", series.name)
        put("face", face)
        put("cardBack", cardBack)
    }

    val back: String get() = cardBack ?: series.back

    override fun restore(data: JsonObject) { }

}

fun restoreCard(data: JsonObject, series: CardSeries? = null): Card {
    val card = Card(
        data.forceGet("name").jsonPrimitive.content,
        series ?: CardSeries.SERIES.getOrThrow(data.forceGet("seriesName").jsonPrimitive.content),
        data.forceGet("face").jsonPrimitive.content,
        data.forceGet("cardBack").jsonPrimitive.contentOrNull,
    )
    card.restore(data)
    return card
}