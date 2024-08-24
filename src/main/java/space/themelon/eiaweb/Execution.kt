package space.themelon.eiaweb

import space.themelon.eia64.runtime.Executor
import java.util.concurrent.atomic.AtomicBoolean

data class Execution(
    val thread: Thread,
    val startTime: Long,
    var fulfilled: AtomicBoolean,
    var executor: Executor,
)