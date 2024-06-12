package db_connection

import dao.CTFDAO
import dao.GroupDAO
import dao.ICTFDAO
import dao.IGroupDAO
import utilities.Console
import utilities.TransactionManager
import javax.sql.DataSource

/**
 * SQLDAOFactory es una fábrica concreta que proporciona implementaciones de DAOs para una base de datos SQL.
 *
 * @property dataSource Fuente de datos utilizada para obtener conexiones a la base de datos.
 * @property console Consola utilizada para mostrar mensajes e información.
 */
class SQLDAOFactory(private val dataSource: DataSource, private val console: Console) : DAOFactory() {

    private var url: String = "jdbc:h2:~/test"
    private var user: String = "user"
    private var password: String = "user"
    private val transactionManager = object : TransactionManager(console, dataSource) {}

    /**
     * Proporciona una implementación de ICTFDAO para manejar las operaciones relacionadas con CTFs.
     * @return Implementación de ICTFDAO.
     */
    override fun getCTFDAO(): ICTFDAO {
        return CTFDAO(console, dataSource, transactionManager)
    }
    /**
     * Proporciona una implementación de IGroupDAO para manejar las operaciones relacionadas con grupos.
     * @return Implementación de IGroupDAO.
     */
    override fun getGroupDAO(): IGroupDAO {
        return GroupDAO(console, dataSource, transactionManager)
    }
}
