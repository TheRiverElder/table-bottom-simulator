package io.github.theriverelder.minigames.tablebottomsimulator

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.ControllerBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import kotlin.math.PI



fun initializeTestCoins(simulator: TableBottomSimulatorServer) {

    val coin15Object = simulator.createAndAddGameObject()
    coin15Object.position = Vector2(100.0, 100.0)
    coin15Object.size = Vector2(100.0, 100.0)
    coin15Object.rotation = 0.2 * PI
    coin15Object.shape = "circle"
    coin15Object.background = "http://localhost:8089/minigames/birmingham/image/common/coin_15.png"
    coin15Object.getOrCreateAndAddBehaviorByType(ControllerBehavior.TYPE).draggable = false

    val coin5Object = simulator.createAndAddGameObject()
    coin5Object.position = Vector2(100.0, 100.0)
    coin5Object.size = Vector2(80.0, 80.0)
    coin5Object.rotation = 0.2 * PI
    coin5Object.shape = "circle"
    coin5Object.background = "http://localhost:8089/minigames/birmingham/image/common/coin_5.png"
//    coin5Object.getOrCreateAndAddBehaviorByType(ControllerBehavior.TYPE).draggable = false

}