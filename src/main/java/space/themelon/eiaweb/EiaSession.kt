package space.themelon.eiaweb

import org.json.JSONObject
import space.themelon.eia64.CompletionHelper
import space.themelon.eia64.runtime.Executor
import java.io.PrintStream

class EiaSession(
    private val callback: (String, String) -> Unit
) {

    // In the future, we can add session time constraints
    // TODO: all the executions of each session MUST be in a new thread

    private val output = EiaOutputStream { callback("output", it) }
    private val executor = Executor().apply {
        standardOutput = PrintStream(output)
        inputCallback = {
            // called when an input is being accepted
            callback("input", "")
        }
    }

    private val completionHelper = CompletionHelper(
        ready = { tokens ->
            runSafely {
                // tells UI to wait for execution before accepting new input
                callback("wait", "")
                executor.loadMainTokens(tokens)
                callback("executed", "") // execution completed
            }
        },
        syntaxError = { callback("error", it) }
    )

    fun newMessage(message: String) {
        println(message)
        val json = JSONObject(message)
        val type = json.getString("type")
        val content = json.getString("message")
        if (type == "code") {
            completionHelper.addLine(content)
        } else {
            executor.standardInput.push(content)
        }
    }

    private fun runSafely(
        block: () -> Unit
    ) {
        try {
            block()
        } catch (e: Exception) {
            callback("error", e.message.toString())
        }
    }
}