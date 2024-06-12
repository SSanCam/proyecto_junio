package db_connection

import dao.ICTFDAO
import dao.IGroupDAO
import utilities.Console
import javax.sql.DataSource

class SQLDAOFactory(private val dataSource: DataSource, private val console: Console) : DAOFactory() {

    private var url: String = "jdbc:h2:~/test"
    private var user: String = "user"
    private var password: String = "user"

    fun connect() {
        // Aquí podrías implementar la lógica para conectarte a tu fuente de datos XML
        console.showInfo("Conectado a la fuente de datos XML en la URL: $url")
    }

    fun disconnect() {
        // Aquí podrías implementar la lógica para desconectarte de tu fuente de datos XML
        console.showInfo("Desconectado de la fuente de datos XML")
    }

    override fun getCTFDAO(): ICTFDAO {
        TODO("Not yet implemented")
    }

    override fun getGroupDAO(): IGroupDAO {
        TODO("Not yet implemented")
    }

}
