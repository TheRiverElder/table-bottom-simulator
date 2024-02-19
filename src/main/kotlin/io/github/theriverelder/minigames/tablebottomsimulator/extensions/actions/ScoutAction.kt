package io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions

import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamGamer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionBase
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOption
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.action.ActionOptions
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.card

class ScoutAction(val birminghamGamer: BirminghamGamer, costCardObjectUid: Int) :
    ActionBase(birminghamGamer.user!!, costCardObjectUid) {

    val extraCardObjectUidList = ArrayList<Int>(2)

    override val options: ActionOptions?
        get() {
            if (extraCardObjectUidList.size >= 2) return null

            val game = birminghamGamer.game
            val options = birminghamGamer.gamer!!.cardObjectUidList
                .filter { it != costCardObjectUid && it !in extraCardObjectUidList }
                .map { ActionOption(game.simulator.gameObjects[it]!!.card.name) { extraCardObjectUidList.add(it) } }
            return ActionOptions("请选择一张手牌丢弃（${extraCardObjectUidList.size + 1}/2）", options)
        }

    override fun reset() {
        extraCardObjectUidList.clear()
    }

    override val fulfilled: Boolean get() = extraCardObjectUidList.size == 2

    override fun perform() {
        val game = birminghamGamer.game
        birminghamGamer.gamer!!.removeCardFromHand(*extraCardObjectUidList.toIntArray())
        val discardedCards = extraCardObjectUidList.mapNotNull { game.simulator.gameObjects[it] }
        val positions = discardedCards.map { it.position }
        discardedCards.forEach { game.discardCard(it) }
        listOfNotNull(
            game.extension.cardSeriesCard.cards["any"],
            game.extension.cardSeriesCard.cards["wild"],
        ).forEachIndexed { index, card ->
            val gameObject = game.simulator.createAndAddGameObject()
            positions.getOrNull(index)?.let { gameObject.position = it; println("position = ${it}, uid = ${gameObject.uid}") }
            gameObject.card = card
            gameObject.shape = "rectangle"
            gameObject.sendUpdateFull()

            birminghamGamer.gamer!!.addCardToHand(gameObject.uid)
        }

        game.simulator.channelGamePlayer.sendGamers()
    }
}