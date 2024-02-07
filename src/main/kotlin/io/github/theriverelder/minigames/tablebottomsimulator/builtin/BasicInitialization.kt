package io.github.theriverelder.minigames.tablebottomsimulator.builtin

import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.CardBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.ControllerBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.PileBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.channel.EditChannel

fun initializeBasic(simulator: TableBottomSimulatorServer) {
    simulator.channels.add(EditChannel("edit", simulator))

    simulator.behaviorTypes.add(ControllerBehavior.TYPE)
    simulator.behaviorTypes.add(PileBehavior.TYPE)
    simulator.behaviorTypes.add(CardBehavior.TYPE)
}