package migration.test

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun getResourceAsFile(filename: String): File {
    val classloader = Thread.currentThread().contextClassLoader
    val inputStream: InputStream = classloader.getResourceAsStream(filename)

    val file = createTempFile(filename)
    FileOutputStream(file).use {
        it.write(inputStream.readBytes())
    }
    return file
}