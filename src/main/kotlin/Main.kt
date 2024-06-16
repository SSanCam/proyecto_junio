import db_connection.SQLDAOFactory
import services.CTFService
import services.GroupService
import utilities.Console
import utilities.ExportFileManagement
import utilities.ProcessCommandManager
import utilities.showHelp

fun main(args: Array<String>) {
    val console = Console()
    val dataSource = SQLDAOFactory.createDataSource()
    val sqlDaoFactory = SQLDAOFactory(dataSource, console)
    val groupService = GroupService(console, sqlDaoFactory.getGroupDAO())
    val ctfService = CTFService(console, sqlDaoFactory.getCTFDAO(), sqlDaoFactory.getGroupDAO())
    val processCommandManager = ProcessCommandManager(console)
    val exportFileManagement = ExportFileManagement(console)

    exportFileManagement.clearFile()

    if (args.isNotEmpty()) {
        if (args[0] == "-h" || args[0] == "--help") {
            showHelp(console)
        } else {
            processCommandManager.processCommand(args, groupService, ctfService)
        }
    } else {
        console.showError("No se proporcionaron argumentos.")
        showHelp(console)
    }
}

