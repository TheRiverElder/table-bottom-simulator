package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.actions.ActionGuide

class BirminghamGame(
    val simulator: TableBottomSimulatorServer,
) {
    val users = Registry(BirminghamUser::uid)

    var currentOrdinal: Int = 0

    val currentUser: BirminghamUser
        get() = users.values.find { it.ordinal == currentOrdinal } ?: throw Exception("Cannot find user with ordinal: $currentOrdinal")

    fun initialize() {
        simulator.users.values.forEachIndexed { index, user -> users.add(BirminghamUser(this, user.uid, index)) }
        prepareUser()
    }
    fun step() {
        currentOrdinal = (currentOrdinal + 1) % users.size
        prepareUser()
    }

    fun prepareUser() {
        for (birminghamUser in users.values) {
            birminghamUser.actionGuide = if (birminghamUser.ordinal == currentOrdinal) ActionGuide(birminghamUser) else null
            birminghamUser.actionGuide?.update()
        }
        listenerGameStateUpdated.emit(this)
    }

    val listenerGameStateUpdated = ListenerManager<BirminghamGame>()

}