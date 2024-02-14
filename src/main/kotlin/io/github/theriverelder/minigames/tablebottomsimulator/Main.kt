package io.github.theriverelder.minigames.tablebottomsimulator

import io.github.theriverelder.minigames.tablebottomsimulator.communication.SimulatorWebSocketServer
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamExtension
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.AUTOSAVE_FILE
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.readFromAutosave
import java.net.InetSocketAddress

fun main(args: Array<String>) {
    val simulator = createSimulator()
    val wss = SimulatorWebSocketServer(simulator, InetSocketAddress(8081))
    wss.start()
}

fun createSimulator(): TableBottomSimulatorServer {
    val simulator = TableBottomSimulatorServer()

    println("=".repeat(32))

    // 注册扩展
    simulator.addExtension(BirminghamExtension(simulator))
    println("加载扩展：${simulator.extensions.size} 个")
    simulator.extensions.values.forEachIndexed { index, ext -> println("$index - ${ext.name}") }

    // 尝试载入自动保存的数据
    println("自动载入存档：$AUTOSAVE_FILE")
    val loadResult = simulator.readFromAutosave()
    if (loadResult) {
        println("载入成功，存档内容：")
        println("GameObject：${simulator.gameObjects.size} 个")
        println("Gamer：${simulator.gamers.size} 个")
    } else {
        println("载入失败，开启全新存档")
        simulator.initialize()
    }

    println("=".repeat(32))

    return simulator
}
