package utilities

import androidx.compose.ui.window.singleWindowApplication
import services.CTFService
import services.GroupService
import ui.CTFManagerApp
import java.io.File
import java.sql.SQLException

class ProcessCommandManager(private val console: Console) {

    fun prosCommFile(filename: String, groupService: GroupService, ctfService: CTFService) {
        val batchFile = File(filename)
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

    fun processCommand(args: Array<String>, groupService: GroupService, ctfService: CTFService) {
        when (args[0]) {
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
            "-p" -> {
                if (args.size != 3) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    try {
                        val groupId = args[1].toInt()
                        val puntuacion = args[2].toInt()
                        ctfService.createCTF(groupId, puntuacion)
                        groupService.updateBestCTF(groupId)
                        console.showInfo("Nuevo registro en CTF creado correctamente.")
                    } catch (e: SQLException) {
                        console.showError("Error al crear CTF: ${e.message}")
                    }
                }
            }
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
            "-e" -> {
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
                if (args.size != 2) {
                    console.showError("Número de parámetros erróneo.")
                } else {
                    prosCommFile(args[1], groupService, ctfService)
                }
            }
            "-i" -> {
                singleWindowApplication {
                    CTFManagerApp(groupService, ctfService)
                }
            }
            else -> console.showError("Comando desconocido: ${args[0]}")
        }
    }
}
