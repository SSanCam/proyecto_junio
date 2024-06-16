package utilities

import androidx.compose.ui.window.singleWindowApplication
import services.CTFService
import services.GroupService
import ui.CTFManagerApp
import java.io.File
import java.sql.SQLException

/**
 * Clase para manejar comandos del usuario.
 *
 * @property console Instancia de Console para mostrar mensajes.
 */
class ProcessCommandManager(private val console: Console) {

    /**
     * Procesa los comandos desde un archivo.
     *
     * @param filename El nombre del archivo que contiene los comandos.
     * @param groupService El servicio de grupo para acceder a los datos de los grupos.
     * @param ctfService El servicio de CTF para acceder a los datos de CTF.
     */
    private fun prosCommFile(filename: String = "C:\\Users\\Sara S Camilleri\\Desktop\\repos\\pro-2324-trim3-SSanCam\\src\\main\\resources\\batchFile.txt", groupService: GroupService, ctfService: CTFService) {
        val batchFile = File(filename)
        console.showInfo("Ruta absoluta del archivo: ${batchFile.absolutePath}")
        if (!batchFile.exists()) {
            console.showError("No se encuentra el archivo de comandos.")
            return
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
     * Procesa los comandos dados como argumentos.
     *
     * @param args Los argumentos de comandos.
     * @param groupService El servicio de grupo para acceder a los datos de los grupos.
     * @param ctfService El servicio de CTF para acceder a los datos de CTF.
     */
    fun processCommand(args: Array<String>, groupService: GroupService, ctfService: CTFService) {
        when (args[0]) {
            "-g" -> {
                // Crear un nuevo grupo
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
            "-p" -> {
                // Añadir o actualizar una participación
                if (args.size != 4) { // Cambiado a 4 argumentos esperados
                    console.showError("Número de parámetros erróneo. Esperados: 4, Recibidos: ${args.size}")
                } else {
                    try {
                        val groupId = args[1].toInt()
                        val ctfId = args[2].toInt()
                        val puntuacion = args[3].toInt()
                        ctfService.createCTF(groupId, puntuacion)
                        groupService.updateBestCTF(groupId)
                        console.showInfo("Nuevo registro en CTF creado correctamente.")
                    } catch (e: SQLException) {
                        console.showError("Error al crear CTF: ${e.message}")
                    }
                }
            }
            "-t" -> {
                // Eliminar un grupo
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
            "-e" -> {
                // Eliminar una participación
                if (args.size != 3) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    try {
                        val ctfId = args[1].toInt()
                        val groupId = args[2].toInt()
                        ctfService.deleteCTFById(ctfId)
                        groupService.updateBestCTF(groupId)
                        console.showInfo("Registro CTF eliminado correctamente.")
                    } catch (e: SQLException) {
                        console.showError("Error al eliminar el CTF: ${e.message}")
                    }
                }
            }
            "-l" -> {
                // Listar información de los grupos
                try {
                    if (args.size == 2) {
                        val groupId = args[1].toIntOrNull()
                        if (groupId != null) {
                            val group = groupService.getGroupByID(groupId)
                            if (group != null) {
                                console.showInfo("Grupo: ${group.groupid}, Descripción: ${group.groupdesc}, Mejor CTF ID: ${group.bestpostctfid ?: "N/A"}")
                                val participations = ctfService.getAllCTFs().filter { it.groupid == groupId }
                                participations.forEach {
                                    console.showInfo("CTF ID: ${it.ctfId}, Puntuación: ${it.score}")
                                }
                            } else {
                                console.showError("Grupo no encontrado.")
                            }
                        } else {
                            console.showError("ID del grupo no válido.")
                        }
                    } else if (args.size == 1) {
                        val groups = groupService.getAllGroups()
                        groups.forEach { group ->
                            console.showInfo("Grupo: ${group.groupid}, Descripción: ${group.groupdesc}, Mejor CTF ID: ${group.bestpostctfid ?: "N/A"}")
                            val participations = ctfService.getAllCTFs().filter { it.groupid == group.groupid }
                            participations.forEach {
                                console.showInfo("CTF ID: ${it.ctfId}, Puntuación: ${it.score}")
                            }
                        }
                    } else {
                        console.showError("Número de parámetros erróneo.")
                    }
                } catch (e: SQLException) {
                    console.showError("Error al obtener la información del grupo: ${e.message}")
                }
            }
            "-c" -> {
                // Listar participaciones en CTFs
                try {
                    if (args.size == 1) {
                        val ctfs = ctfService.getAllCTFs()
                        if (ctfs.isNotEmpty()) {
                            ctfs.sortedByDescending { it.score }.forEach { ctf ->
                                val group = groupService.getGroupByID(ctf.groupid)
                                val groupName = group?.groupdesc ?: "Desconocido"
                                console.showInfo("CTF ID: ${ctf.ctfId}, Grupo: $groupName, Puntuación: ${ctf.score}")
                            }
                        } else {
                            console.showInfo("No hay participaciones registradas en los CTFs.")
                        }
                    } else {
                        console.showError("Número de parámetros erróneo.")
                    }
                } catch (e: SQLException) {
                    console.showError("Error al obtener la información de CTFs: ${e.message}")
                }
            }
            "-f" -> {
                // Procesar comandos desde un archivo
                try {
                    val filename = if (args.size == 2) args[1] else "C:\\Users\\Sara S Camilleri\\Desktop\\repos\\pro-2324-trim3-SSanCam\\src\\main\\resources\\batchFile.txt"
                    console.showInfo("Procesando archivo de comandos: $filename")
                    prosCommFile(filename, groupService, ctfService)
                } catch (e: Exception) {
                    console.showError("No se encuentra el archivo: ${e.message}")
                }
            }
            "-i" -> {
                // Iniciar la interfaz gráfica
                singleWindowApplication {
                    CTFManagerApp(groupService, ctfService)
                }
            }
            else -> console.showError("Comando desconocido: ${args[0]}")
        }
    }
}
