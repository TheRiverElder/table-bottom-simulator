package io.github.theriverelder.minigames.tablebottomsimulator

import java.net.InetSocketAddress

fun main(args: Array<String>) {
    val wss = SimulatorWebSocketServer(InetSocketAddress(8081))
    wss.start()
}
