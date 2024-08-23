package space.themelon.eiaweb

import java.io.ByteArrayOutputStream
import java.io.OutputStream

class EiaOutputStream(
    private val callback: (String) -> Unit
): OutputStream() {

    private val buffer = ByteArrayOutputStream()

    override fun write(b: Int) {
        if (b == 10) {
            // a new line
            callback(buffer.toString())
            buffer.reset()
        } else {
            buffer.write(b)
        }
    }

    fun pushRemainingBuffer() {
        if (buffer.size() > 0) {
            callback(buffer.toString())
            buffer.reset()
        }
    }
}