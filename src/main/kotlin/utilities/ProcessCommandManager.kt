package utilities

import dao.CTFDAO
import services.GroupService
import java.io.File

class ProcessCommandManager(private val console: Console) {

    fun prosCommFile(filename: String, groupService: GroupService, ctfDAO: CTFDAO) {
        File(filename).useLines { lines ->
            lines.filter { it.isNotBlank() && !it.startsWith("#") }
                .forEach { line ->
                    val parts = line.split(";")
                    processCommand(parts.toTypedArray(), groupService, ctfDAO)
                }
        }
    }

    private fun processCommand(args: Array<String>, groupService: GroupService, ctfdao: CTFDAO) {

        when (args[0]) {
            // Agrega nuevo registro a GRUPOS
            "-g" -> {
                if (args.size != 3) {
                    console.showError("ERROR: El parámetro <grupoid> debe ser un valor numérico de tipo entero.")
                } else {
                    groupService.createGroup(args[2])
                    console.showInfo("Nuevo registro agregado a GRUPOS.")
                }
            }

            // Agrega un nuevo registro a CTFS
            "-p" -> {

            }

            // Elimina un registro de GRUPOS
            "-t" -> {

            }

            // Elimina un registro de CTFS
            "-e" -> {

            }

            // Muestra un registro de GRUPOS
            "-l" -> {

            }

            // Muestra todos los registros de CTFS
            "-c" -> {

            }

            // Fichero con conjunto de comandos para procesamiento por lotes
            "-f" -> {

            }

            // Lanza la interfaz gráfica
            "-i" -> {

            }

            else -> console.showError("Unknown command: ${args[0]}")
        }
    }
}