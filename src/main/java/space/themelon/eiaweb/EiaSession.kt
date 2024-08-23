package space.themelon.eiaweb

import org.java_websocket.WebSocket
import org.json.JSONObject
import space.themelon.eia64.CompletionHelper
import space.themelon.eia64.runtime.Executor
import java.io.PrintStream

class EiaSession(
    private val webSocket: WebSocket
) {

    private fun send(type: String, message: String) {
        webSocket.send(
            JSONObject()
                .put("type", type)
                .put("message", message)
                .toString()
        )
    }

    // In the future, we can add session time constraints
    // TODO: all the executions of each session MUST be in a new thread

    private val output = EiaOutputStream { line ->
        send("output", line)
    }

    private val executor = Executor().apply {
        standardOutput = PrintStream(output)
    }

    private val completionHelper = CompletionHelper(
        ready = { tokens ->
            runSafely {
                executor.loadMainTokens(tokens)
            }
        },
        syntaxError = { send("error", it) }
    )

    fun newMessage(message: String) {
        val json = JSONObject(message)
        val type = json.getString("type")
        if (type == "code") {
            completionHelper.addLine(json.getString("message"))
        } else {
            // TODO:
            //  We
        }
    }

    private fun runSafely(
        block: () -> Unit
    ) {
        try {
            block()
        } catch (e: Exception) {
            send("error", e.message.toString())
        }
    }
}