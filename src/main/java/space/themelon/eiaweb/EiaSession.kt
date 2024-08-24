package space.themelon.eiaweb

import org.json.JSONObject
import space.themelon.eia64.CompletionHelper
import space.themelon.eia64.runtime.Executor
import space.themelon.eia64.syntax.Token
import java.io.PrintStream

class EiaSession(
    private val callback: (String, String) -> Unit
) {

    // In the future, we can add session time constraints
    // TODO: all the executions of each session MUST be in a new thread

    private var awaitingInput = false


    private val output = EiaOutputStream { callback("output", it) }
    private val executor = Executor().apply {
        standardOutput = PrintStream(output)
        inputCallback = {
            awaitingInput = true
            // called when an input is being accepted
            output.pushRemainingBuffer()
            callback("input", "")
        }
    }

    private val parallelExecutor = ParallelExecutor(
        callback = { tokens ->
            // called back under a new thread
            callback("wait", "")
            runSafely {
                if (tokens is ArrayList<*>) {
                    executor.loadMainTokens(tokens as List<Token>)
                } else {
                    executor.loadFileSource(tokens as String)
                }
            }
            callback("executed", "") // execution completed
        },
    )

    private val completionHelper = CompletionHelper(
        ready = { tokens ->
            parallelExecutor.tokens = tokens
        },
        syntaxError = { callback("error", it) }
    )

    fun newMessage(message: String) {
        val json = JSONObject(message)
        val type = json.getString("type")
        val content = json.getString("data")
        println(json)
        if (type == "code") {
            if (awaitingInput) {
                executor.standardInput.push(content)
                awaitingInput = false
            } else {
                completionHelper.addLine(content)
            }
        } else {
            // a file!
            parallelExecutor.tokens = content
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