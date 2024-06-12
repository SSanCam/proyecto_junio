package main

import db_connection.DAOFactory
import db_connection.DataSourceType
import org.h2.jdbcx.JdbcDataSource
import services.CTFService
import services.GroupService
import utilities.Console
import utilities.ProcessCommandManager

fun main(args: Array<String>) {
    val console = Console()

    // Configura la conexión a la base de datos H2
    val dataSource = JdbcDataSource().apply {
        setURL("jdbc:h2:~/test")
        user = "sa"
        password = ""
    }

    // Configura los DAOs
    val daoFactory = DAOFactory.getFactory(DataSourceType.H2, dataSource, console)

    val groupDAO = daoFactory.getGroupDAO()
    val ctfDAO = daoFactory.getCTFDAO()

    // Configura los servicios
    val groupService = GroupService(console, groupDAO)
    val ctfService = CTFService(console, ctfDAO)

    // Configura el ProcessCommandManager
    val processCommandManager = ProcessCommandManager(console)

    if (args.isNotEmpty() && args[0] == "-f") {
        if (args.size == 2) {
            val filename = args[1]
            processCommandManager.prosCommFile(filename, groupService, ctfService)
        } else {
            console.showError("ERROR: El número de parámetros no es adecuado.")
        }
    } else {
        console.showError("ERROR: Comando no reconocido o falta de parámetros.")
    }
}
