package space.themelon.eiaweb

import space.themelon.eia64.syntax.Token
import kotlin.concurrent.thread

class ParallelExecutor(
    private val callback: (tokens: List<Token>) -> Unit,
) {
    var tokens: List<Token>? = null

    init {
        thread {
            doWork()
        }
    }

    private fun doWork() {
        while (true) {
            if (tokens != null) {
                callback(tokens!!)
                tokens = null
            }
            Thread.sleep(50)
        }
    }
}