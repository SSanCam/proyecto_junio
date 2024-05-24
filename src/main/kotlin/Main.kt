
import dao.CTFDAO
import dao.GroupDAO
import db_connection.DataSourceFactory
import services.GroupService
import utilities.Console
import java.io.File

fun main(args: Array<String>) {
    val console = Console()
    val dataSource = DataSourceFactory.getDS(DataSourceFactory.DataSourceType.H2)
    val groupDAO = GroupDAO(console, dataSource)
    val groupService = GroupService(console, groupDAO)
    val ctfDAO = CTFDAO(console, dataSource)

    if (args.isEmpty()) {
        console.showInfo("No commands provided. Exiting...")
        return
    }

    if (args[0] == "-f" && args.size == 2) {
        processCommandFile(args[1], groupService, ctfDAO)
    } else {
        processCommand(args, groupService, ctfDAO)
    }
}

fun processCommandFile(filename: String, groupService: GroupService, ctfDAO: CTFDAO) {
    File(filename).useLines { lines ->
        lines.filter { it.isNotBlank() && !it.startsWith("#") }
            .forEach { line ->
                val parts = line.split(";")
                processCommand(parts.toTypedArray(), groupService, ctfDAO)
            }
    }
}

fun processCommand(args: Array<String>, groupService: GroupService, ctfdao: CTFDAO) {
    val console = Console()
    when (args[0]) {
        "-g" -> {
            // Agrega nuevo registro a GRUPOS
            if (args.size != 3) {
                console.showError("ERROR: El parámetro <grupoid> debe ser un valor numérico de tipo entero.")
            } else {
                groupService.createGroup(args[2])
            }
        }

        "-p" -> {
            // Agrega un nuevo registro a CTFS
        }

        "-t" -> {
            // Elimina un registro de GRUPOS
        }

        "-e" -> {
            // Elimina un registro de CTFS
        }

        "-l" -> {
            // Muestra un registro de GRUPOS
        }
        "-c" -> {
            // Muestra todos los registros de CTFS
        }
        "-f" -> {
            // Fichero con conjunto de comandos para procesamiento por lotes
        }
        "-i" -> {
            // Lanza la interfaz gráfica
        }
        else -> println("Unknown command: ${args[0]}")
    }
}

