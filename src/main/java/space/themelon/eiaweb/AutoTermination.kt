package space.themelon.eiaweb

import java.util.*
import kotlin.concurrent.thread

object AutoTermination {
    // Auto terminating any code that runs for more than 1 min
    private val EXECUTIONS = Collections.synchronizedList(ArrayList<Execution>())

    fun start() {
        thread {
            while (true) {
                synchronized(EXECUTIONS) {
                    val completed = ArrayList<Execution>()
                    EXECUTIONS.forEach {
                        if (it.fulfilled.get()) {
                            completed.add(it)
                        } else {
                            if (System.currentTimeMillis() - it.startTime > 60 * 1000) {
                                // above 10 seconds, shouldn't be
                                completed.add(it)

                                // stop current execution and recreate
                                it.executor.shutdownEvaluator()
                                it.thread.interrupt()
                                it.executor.recreateEvaluator()
                            }
                        }
                    }
                    EXECUTIONS.removeAll(completed)
                }
                Thread.sleep(10 * 1000)
            }
        }
    }

    fun limit(execution: Execution) {
        synchronized(EXECUTIONS) {
            EXECUTIONS.add(execution)
        }
    }
}