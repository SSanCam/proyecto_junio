package utilities

import services.CTFService
import services.GroupService
import java.io.File
import java.sql.SQLException

/**
 * ProcessCommandManager se encarga de procesar los comandos recibidos desde la línea de comandos o desde un archivo.
 *
 * @property console Instancia de Console para mostrar mensajes.
 */

class ProcessCommandManager(private val console: Console) {

    /**
     * Procesa un archivo de comandos.
     *
     * @param filename Nombre del archivo que contiene los comandos.
     * @param groupService Servicio para manejar operaciones relacionadas con grupos.
     * @param ctfService Servicio para manejar operaciones relacionadas con CTFs.
     */
    fun prosCommFile(filename: String, groupService: GroupService, ctfService: CTFService) {

        val batchFile = File("\\resources\\batchFile.txt")
        if (!batchFile.exists()){
            batchFile.createNewFile()
        }

        batchFile.useLines { lines ->
            lines.filter { it.isNotBlank() && !it.startsWith("#") }
                .forEach { line ->
                    val parts = line.split(";")
                    processCommand(parts.toTypedArray(), groupService, ctfService)
                }
        }
    }

    /**
     * Procesa un comando individual.
     *
     * @param args Array de argumentos del comando.
     * @param groupService Servicio para manejar operaciones relacionadas con grupos.
     * @param ctfService Servicio para manejar operaciones relacionadas con CTFs.
     */
    private fun processCommand(args: Array<String>, groupService: GroupService, ctfService: CTFService) {
        when (args[0]) {
            // Agrega nuevo registro a GRUPOS
            "-g" -> {
                if (args.size != 2) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    try {
                        groupService.createGroup(args[1])
                        console.showInfo("Nuevo registro agregado a GRUPOS.")
                    } catch (e: SQLException) {
                        console.showError("Error al crear un nuevo grupo: ${e.message}")
                    }
                }
            }
            // Agrega un nuevo registro a CTFS
            "-p" -> {
                if (args.size != 3) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    try {
                        val groupId = args[1].toInt()
                        val puntuacion = args[3].toInt()
                        ctfService.createCTF(groupId, puntuacion)
                        groupService.updateBestCTF(groupId)
                        console.showInfo("Nuevo registro en CTF creado correctamente.")
                    } catch (e: SQLException) {
                        console.showError("Error al crear CTF: ${e.message}")
                    }
                }
            }

            // Elimina un registro de GRUPOS
            "-t" -> {
                if (args.size != 2) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    try {
                        val groupId = args[1].toInt()
                        groupService.deleteGroup(groupId)
                        console.showInfo("Grupo eliminado correctamente.")
                    } catch (e: SQLException) {
                        console.showError("Error al eliminar el grupo: ${e.message}")
                    }
                }
            }

            // Elimina un registro de CTFS
            "-e" -> {
                if (args.size != 3) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    try {
                        val ctfId = args[1].toInt()
                        ctfService.deleteCTFById(ctfId)
                        console.showInfo("Registro CTF eliminado correctamente.")
                    } catch (e: SQLException) {
                        console.showError("Error al eliminar el CTF: ${e.message}")
                    }
                }
            }

            // Muestra un registro de GRUPOS
            "-l" -> {
                if (args.size != 2) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    try {
                        val groupId = args[1].toInt()
                        val group = groupService.getGroupByID(groupId)
                        if (group != null) {
                            console.showInfo(group.toString())
                        }
                    } catch (e: SQLException) {
                        console.showError("Error al recibir los datos de GRUPOS.")
                    }
                }
            }

            // Muestra todos los registros de CTFS
            "-c" -> {
                if (args.size != 2) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    try {
                        val ctfs = ctfService.getAllCTFs().toString()
                        console.showInfo(ctfs)
                    } catch (e: SQLException) {
                        console.showError("Error al obtener la información de CTFs.")
                    }
                }
            }

            // Fichero con conjunto de comandos para procesamiento por lotes
            "-f" -> {
                if (args.size != 2) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    val batchFile = File(args[1])
                    prosCommFile(args[1], groupService, ctfService)
                }
            }

            // Lanza la interfaz gráfica
            "-i" -> {
                TODO("INTERFAZ_GRAFICA")
            }
            else -> console.showError("Unknown command: ${args[0]}")
        }
    }
}
