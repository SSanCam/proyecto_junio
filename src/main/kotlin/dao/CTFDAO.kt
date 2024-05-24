package dao

import entitiesDAO.CTFEntity
import utilities.Console
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Clase para manejar las operaciones de base de datos para las competiciones CTF.
 * Implementa la interfaz ICTFDao para asegurar que todos los métodos necesarios están implementados.
 *
 * @property console Instancia de Console para mostrar mensajes de error.
 * @property dataSource Fuente de datos JDBC para la conexión a la base de datos.
 */
class CTFDAO(private val console: Console, private val dataSource: DataSource) : ICTFDAO {

    // Agrega un nuevo CTF a la base de datos
    override fun addCTF(ctf: CTFEntity) {
        val sql = """INSERT INTO CTFS (CTFif, grupoid, puntuacion) 
            |VALUES (?, ?, ?);""".trimMargin()
        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, ctf.CTFid)
                    stmt.setInt(2, ctf.groupid)
                    stmt.setInt(3, ctf.puntuacion)
                    stmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al agregar CTF: ${e.message}")
            throw e
        }
    }

    // Recupera los datos de un registro CTF por su ID
    override fun getCTFById(id: Int): CTFEntity? {
        val sql = """
            |SELECT * 
            |FROM CTF
            |WHERE CTFid =? ;
        """.trimMargin()
        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    val result = stmt.executeQuery()
                    if (result.next()) {
                        return CTFEntity(
                            CTFid = result.getInt("CTFid"),
                            groupid = result.getInt("grupoid"),
                            puntuacion = result.getInt("puntuacion")
                        )
                    }
                }
            }
        } catch (e: SQLException) {
            console.showError("Error en la recuperación de los datos: ${e.message}")
            throw e
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
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, groupId)
                    stmt.setInt(2, newScore)
                    stmt.setInt(3, ctfId)
                    stmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al actualizar el CTF: ${e.message}")
            throw e
        }
    }

    // Elimina un CTF de la base de datos usando su ID
    override fun deleteCTFById(id: Int) {
        val sql = """
             |DELETE FROM CTFS
             |WHERE CTFid =? ;
             """.trimIndent()
        try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    stmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al eliminar el CTF: ${e.message}")
            throw e
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
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val result = stmt.executeQuery()
                    while (result.next()) {
                        ctfs.add(
                            CTFEntity(
                                CTFid = result.getInt("CTFid"),
                                groupid = result.getInt("grupoid"),
                                puntuacion = result.getInt("puntuacion")
                            )
                         )
                    }
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al recibir los datos: ${e.message}")
            throw e
        }
        return ctfs
    }

}