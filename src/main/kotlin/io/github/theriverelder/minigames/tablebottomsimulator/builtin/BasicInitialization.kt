package io.github.theriverelder.minigames.tablebottomsimulator.builtin

import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.ControllerBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.PileBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.channel.EditChannel
import io.github.theriverelder.minigames.tablebottomsimulator.genUid
import io.github.theriverelder.minigames.tablebottomsimulator.user.User

fun initializeBasic(simulator: TableBottomSimulatorServer, userCount: Int) {
    simulator.channels.add(EditChannel("edit", simulator))

    simulator.behaviorTypes.add(ControllerBehavior.TYPE)
    simulator.behaviorTypes.add(PileBehavior.TYPE)
    simulator.behaviorTypes.add(CardBehavior.TYPE)

//    simulator.gameObjects.onAdd.add { it.createAndAddBehavior(ControllerBehavior.TYPE) }

    for (i in 0 until userCount) {
        val uid = simulator.genUid()
        val user = User(simulator, uid, "User$uid")
        simulator.users.add(user)
    }
}