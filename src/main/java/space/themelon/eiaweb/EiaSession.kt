package space.themelon.eiaweb

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import space.themelon.eia64.CompletionHelper
import space.themelon.eia64.runtime.Executor
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
            callback("input", "")
        }
    }

    private val parallelExecutor = ParallelExecutor(
        callback = { tokens ->
            // called back under a new thread
            callback("wait", "")
            runSafely {
                executor.loadMainTokens(tokens)
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
        if (awaitingInput) {
            executor.standardInput.push(message)
            awaitingInput = false
        } else {
            completionHelper.addLine(message)
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