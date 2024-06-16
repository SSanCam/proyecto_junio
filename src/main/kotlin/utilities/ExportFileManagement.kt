package utilities

import java.io.File
import java.io.IOException

/**
 * Clase para manejar la exportación de archivos.
 *
 * @property console Instancia de Console para mostrar mensajes.
 * @property filePath Ruta del archivo donde se exportarán los datos.
 */
class ExportFileManagement(private val console: Console, val filePath: String = "C:\\Users\\Sara S Camilleri\\Desktop\\repos\\pro-2324-trim3-SSanCam\\src\\main\\resources\\clasificacion_ctfs.txt") {
    private val file: File = File(filePath)

    init {
        try {
            val parentDir = file.parentFile
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
                console.showInfo("Archivo para exportaciones creado en ${file.absolutePath}.")
            }
        } catch (e: IOException) {
            console.showError("Error al crear el archivo de exportaciones: ${e.message}")
        }
    }

    fun clearFile() {
        try {
            file.writeText("")
            console.showInfo("Archivo de exportación listo para usar en ${file.absolutePath}.")
        } catch (e: IOException) {
            console.showError("Error al limpiar el archivo: ${e.message}")
        }
    }

    fun writeToFile(text: String) {
        try {
            file.appendText(text)
            console.showInfo("Se ha escrito la información exportada en ${file.absolutePath}.")
        } catch (e: IOException) {
            console.showError("Error al escribir en el archivo de exportación: ${e.message}")
        }
    }
}
