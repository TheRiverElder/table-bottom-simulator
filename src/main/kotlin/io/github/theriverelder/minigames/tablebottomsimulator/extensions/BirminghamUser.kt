package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.ActionGuide
import io.github.theriverelder.minigames.tablebottomsimulator.user.User

class BirminghamUser(
    val game: BirminghamGame,
    val uid: Int,
    val ordinal: Int,
    var money: Int = 0,
) {

    var actionGuide: ActionGuide? = null

    val simulatorUser: User
        get() = game.simulator.users[uid] ?: throw Exception("Cannot find user with uid: $uid")
}