package space.themelon.eiaweb

import kotlin.concurrent.thread

class ParallelExecutor(
    private val callback: (tokens: Any) -> Unit,
) {
    var tokens: Any? = null

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