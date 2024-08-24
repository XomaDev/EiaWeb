package space.themelon.eiaweb

import space.themelon.eia64.runtime.Executor
import java.io.File
import java.net.InetSocketAddress

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val port = args[0].toInt()
        println("Running on port $port")
        Executor.DEBUG = false
        Executor.STD_LIB = File(System.getProperty("user.dir"), "stdlib/").absolutePath
        val server = ShellWebSocket(InetSocketAddress(port))
        server.start()
    }
}