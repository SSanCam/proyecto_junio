package test

import java.io.File
import java.io.IOException

fun main() {
    val filePath = "C:\\Users\\Sara S Camilleri\\Desktop\\repos\\pro-2324-trim3-SSanCam\\src\\main\\resources\\clasificacion_ctfs.txt"
    val file = File(filePath)

    try {
        if (!file.exists()) {
            file.createNewFile()
            println("Archivo creado correctamente.")
        }
        file.appendText("Verificación de escritura exitosa.\n")
        println("Texto escrito correctamente en el archivo.")
    } catch (e: IOException) {
        println("Error al escribir en el archivo: ${e.message}")
    }
}
