package space.themelon.eiaweb

import space.themelon.eia64.runtime.Executor
import java.io.File
import java.net.InetSocketAddress

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Executor.DEBUG = false
        Executor.STD_LIB = File(System.getProperty("user.dir"), "stdlib/").absolutePath
        val server = ShellWebSocket(InetSocketAddress(9191))
        server.start()
    }
}