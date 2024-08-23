package space.themelon.eiaweb

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.json.JSONObject
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class ShellWebSocket(address: InetSocketAddress) : WebSocketServer(address) {

    companion object {
        private val SESSION_LOOKUP = HashMap<WebSocket, EiaSession>()
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake) {
        println("Web Socket Opened")
        conn?.let {
            SESSION_LOOKUP[it] = EiaSession { type, message ->
                conn.send(
                    JSONObject()
                        .put("type", type)
                        .put("message", message)
                        .toString()
                )
            }
        }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String, remote: Boolean) {
        SESSION_LOOKUP.remove(conn)
    }

    override fun onMessage(conn: WebSocket?, message: String) {
        println(message)
        SESSION_LOOKUP[conn]?.newMessage(message)
    }

    override fun onMessage(conn: WebSocket?, message: ByteBuffer) {
        println("Message buffer received $message")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        println("Error Occurred: $ex")
        SESSION_LOOKUP.remove(conn)
    }

    override fun onStart() {
        println("Eia Web Socket Started")
    }
}