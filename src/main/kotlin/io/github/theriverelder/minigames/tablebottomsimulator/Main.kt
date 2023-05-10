package io.github.theriverelder.minigames.tablebottomsimulator

import io.github.theriverelder.minigames.lib.math.Vector2
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.ControllerBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.initializeBasic
import io.github.theriverelder.minigames.tablebottomsimulator.channel.ControlChannel
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.lang.Exception
import kotlin.math.PI

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)



fun Application.module() {
    configureRouting()
    configureSockets()
}

fun Application.configureRouting() {
    routing {
    }
}

fun Application.configureSockets() {
    install(WebSockets)

    val userSessions = HashMap<Int, WebSocketSession>()

    val simulator = initializeSimulator()
    val communication = Communication(simulator) { data, receiver ->
        val receiverUserUid = receiver.uid
        val session = userSessions[receiverUserUid] ?: return@Communication
        session.send(data)
    }
    simulator.communication = communication

    routing {
        webSocket("/minigames/tbs") {
            println("Connection in")

            val userUidString = call.parameters["userUid"] ?: run {
                close(CloseReason(200, "No userUid"))
                println("Connection error: No userUid")
                return@webSocket
            }

            val userUid = userUidString.toInt()

            if (userUid in userSessions) {
                close(CloseReason(200, "User #${userUid} already in"))
                println("Connection error: User #${userUid} already in")
                return@webSocket
            }

            val user = simulator.users[userUid] ?: run {
                close(CloseReason(200, "No user with uid: $userUid"))
                println("Connection error: No user with uid: $userUid")
                return@webSocket
            }

            userSessions[userUid] = this@webSocket

            println("User in: #${user.uid} ${user.name} ${user.color}")

            simulator.channelFullUpdateChannel.sendFullUpdate(user)

            for(frame in incoming) {
                try {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    communication.receiveRawData(receivedText, user)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            userSessions.remove(userUid, this@webSocket)

            println("User out #${user.uid} ${user.name} ${user.color}")
            println("Connection out")
        }
    }
}

fun initializeSimulator(): TableBottomSimulatorServer {
    val simulator = TableBottomSimulatorServer()
    initializeBasic(simulator, 2)
    initializeTest(simulator)

    println("Valid users: ")
    simulator.users.values.forEach { println("#${it.uid} ${it.name} ${it.color}") }

    return simulator
}

fun initializeTest(simulator: TableBottomSimulatorServer) {

    val mapObject = simulator.createAndAddGameObject()
    mapObject.size = Vector2(4000.0, 4000.0)
    mapObject.position = Vector2.zero()
    mapObject.background = "http://localhost:8089/minigames/birmingham/images/common/map.jpg"
    mapObject.shape = "rectangle"
    mapObject.getOrCreateAndAddBehaviorByType(ControllerBehavior.TYPE).draggable = false
    
    val coinObject = simulator.createAndAddGameObject()
    coinObject.position = Vector2(100.0, 100.0)
    coinObject.size = Vector2(100.0, 100.0)
    coinObject.rotation = 0.2 * PI
    coinObject.shape = "circle"
    coinObject.background = "http://localhost:8089/minigames/birmingham/images/common/coin_15.png"

}