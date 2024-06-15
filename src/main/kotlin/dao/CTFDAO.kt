package dao

import entitiesDAO.CTFEntity
import utilities.Console
import utilities.TransactionManager
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Clase para manejar las operaciones de base de datos para las competiciones CTF.
 * Implementa la interfaz ICTFDao para asegurar que todos los métodos necesarios están implementados.
 *
 * @property console Instancia de Console para mostrar mensajes de error.
 * @property dataSource Fuente de datos JDBC para la conexión a la base de datos.
 * @property transaction Instancia de TransactionManager para manejar transacciones.
 */

class CTFDAO(
    private val console: Console,
    private val dataSource: DataSource,
    private val transaction: TransactionManager
) : ICTFDAO {

    // Agrega un nuevo CTF a la base de datos
    override fun createCTF(ctf: CTFEntity) {
        val sql = """INSERT INTO CTFS (grupoid, puntuacion) 
            |VALUES (?, ?);""".trimMargin()
        try {
            transaction.openConnection()
            transaction.commit()
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, ctf.groupid)
                    stmt.setInt(2, ctf.score)
                    stmt.executeUpdate()
                }
                transaction.commit()
                console.showInfo("CTF agregado correctamente.")
            }
        } catch (e: SQLException) {
            transaction.rollback()
            console.showError("Error al agregar CTF: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
    }

    // Recupera los datos de un registro CTF por su ID
    override fun getCTFById(id: Int): CTFEntity? {
        val sql = """
            |SELECT * 
            |FROM CTFS
            |WHERE CTFid = ? ;
        """.trimMargin()
        try {
            transaction.openConnection()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val result = stmt.executeQuery()
                if (result.next()) {
                    return CTFEntity(
                        ctfId = result.getInt("CTFid"),
                        groupid = result.getInt("grupoid"),
                        score = result.getInt("puntuacion")
                    )
                }
            }
        } catch (e: SQLException) {
            console.showError("Error en la recuperación de los datos: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
        return null
    }

    // Actualiza los detalles de un CTF existente
    override fun updateCTF(ctfId: Int, groupId: Int, newScore: Int) {
        val sql = """
            |UPDATE CTFS
            |SET grupoid = ?, puntuacion =?
            |WHERE CTFid = ?;
        """.trimIndent()
        try {
            transaction.openConnection()
            transaction.commit()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, newScore)
                stmt.setInt(2, ctfId)
                stmt.setInt(3, groupId)
                stmt.executeUpdate()
            }
            transaction.commit()
            console.showInfo("CTF actualizado correctamente.")
        } catch (e: SQLException) {
            transaction.rollback()
            console.showError("Error al actualizar el CTF: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
    }

    // Elimina un CTF de la base de datos usando su ID
    override fun deleteCTFById(id: Int) {
        val sql = """
             |DELETE FROM CTFS
             |WHERE CTFid =? ;
             """.trimIndent()
        try {
            transaction.openConnection()
            transaction.commit()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeUpdate()
            }
            transaction.commit()
            console.showInfo("CTF eliminado correctamente.")
        } catch (e: SQLException) {
            console.showError("Error al eliminar el CTF: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
    }

    // Devuelve una lista con todos los registros de CTF existentes en la base de datos.
    override fun getAllCTFs(): List<CTFEntity> {
        val ctfs = mutableListOf<CTFEntity>()
        val sql = """
            |SELECT *
            |FROM CTFS;
        """.trimIndent()
        try {
            transaction.openConnection()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                val result = stmt.executeQuery()
                while (result.next()) {
                    ctfs.add(
                        CTFEntity(
                            ctfId = result.getInt("CTFid"),
                            groupid = result.getInt("grupoid"),
                            score = result.getInt("puntuacion")
                        )
                    )
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al recibir los datos: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
        return ctfs
    }
}
