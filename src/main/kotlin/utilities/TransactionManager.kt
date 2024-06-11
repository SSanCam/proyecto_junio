package utilities

import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Clase para manejar transacciones de la base de datos.
 *
 * @property console Instancia de Console para mostrar mensajes.
 * @property dataSource Fuente de datos para las conexiones a la base de datos.
 */

abstract class TransactionManager(private val console: Console, private val dataSource: DataSource) {
    private var connection: Connection? = null

    /**
     * Abre la conexión con la base de datos.
     */
    fun openConnection() {
        try {
            connection = dataSource.connection
            connection?.autoCommit = false
        } catch (e: SQLException) {
            console.showError("Error al intentar conectar con la base de datos: ${e.message}")
            throw e
        }
    }

    /**
     * Cierra la conexión con la base de datos.
     */
    fun closeConnection() {
        try {
            connection?.close()
        } catch (e: SQLException) {
            console.showError("Error al cerrar la conexión.")
        } finally {
            connection = null
        }
    }

    /**
     * Realiza el commit de la transacción actual.
     */
    fun commit() {
        try {
            connection?.commit()
        } catch (e: SQLException) {
            console.showError("Error al realizar commit: ${e.message}")
            throw e
        }
    }

    /**
     * Realiza el rollback de la transacción actual.
     */
    fun rollback() {
        try {
            connection?.rollback()
        } catch (e: SQLException) {
            console.showError("Error al realizar rollback: ${e.message}")
            throw e
        }
    }

    /**
     * Obtiene la conecxión actual.
     */
    fun getConnection() : Connection {
        return connection ?: throw IllegalArgumentException("La conexión no está abierta.")
    }
}