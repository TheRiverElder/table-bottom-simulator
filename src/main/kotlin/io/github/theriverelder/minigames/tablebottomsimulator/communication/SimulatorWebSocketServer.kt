package io.github.theriverelder.minigames.tablebottomsimulator.communication

import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.coroutines.runBlocking
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.net.URI

class SimulatorWebSocketServer(
    val simulator: TableBottomSimulatorServer,
    address: InetSocketAddress?,
) : WebSocketServer(address) {

    inner class Conn(
        val session: WebSocket,
        val user: User,
    ) {
        fun unregister() {
            userUidMap.remove(user.uid)
            sessionMap.remove(session)
        }

        fun register() {
            userUidMap[user.uid] = this
            sessionMap[session] = this
        }
    }

    val userUidMap = HashMap<Int, Conn>()
    val sessionMap = HashMap<WebSocket, Conn>()


    lateinit var communication: Communication

    override fun onStart() {
        println("Server started at port $port")

        communication = Communication(simulator) { data, receiver ->
            val receiverUserUid = receiver.uid
            val session = userUidMap[receiverUserUid]?.session ?: return@Communication
            session.send(data)
        }
        simulator.communication = communication
    }

    override fun onOpen(session: WebSocket, handshake: ClientHandshake) {
        val uri = URI(handshake.resourceDescriptor)
        if (uri.path != "/minigames/tbs") return

        val queryData = HashMap<String, String>(2)

        uri.query.split('&').forEach {
            val parts = it.split("=", limit = 2)
            val key = parts[0]
            val value = parts.getOrNull(1) ?: true.toString()
            queryData[key] = value
        }

        val userUidString = queryData["userUid"] ?: run {
            session.close(1002, "No userUid")
            println("Connection error: No userUid")
            return
        }

        val userUid = userUidString.toInt()

        if (userUid in userUidMap) {
            userUidMap[userUid]?.session?.close(1002, "User #${userUid} reconnect in")
        }

        val user = simulator.users[userUid] ?: run {
            val user = User(simulator, userUid)
            simulator.users.add(user)
            user
        }

        Conn(session, user).register()

        println("Connected: ${session.remoteSocketAddress}")
        println("User in: #${user.uid} ${user.name} ${user.gamer?.color}")

        simulator.channelCard.sendCardSerieses(user)
        simulator.channelFullUpdateChannel.sendFullUpdate(user)

    }

    override fun onMessage(session: WebSocket, message: String) {
        try {
            val conn = sessionMap[session] ?: return

            runBlocking { // 希望能阻止各种意外bug
                communication.receiveRawData(message, conn.user)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: Error) {
            e.printStackTrace()
        }
    }

    override fun onClose(session: WebSocket, code: Int, reason: String?, remote: Boolean) {
        val conn = sessionMap[session] ?: return
        val user = conn.user

        conn.unregister()
        println("User out #${user.uid} ${user.name} ${user.gamer?.color}")
        println("Disconnected: ${session.remoteSocketAddress} $reason")
    }

    override fun onError(session: WebSocket, ex: Exception) {
        System.err.println(ex)
    }
}