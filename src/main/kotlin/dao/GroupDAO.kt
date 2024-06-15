package dao

import entitiesDAO.CTFEntity
import entitiesDAO.GroupEntity
import utilities.Console
import utilities.TransactionManager
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Clase que implementa la interfaz IGroupDAO para manejar operaciones de la base de datos
 * relacionadas con los grupos.
 *
 * @property console Instancia de Console para mostrar mensajes de error.
 * @property dataSource Fuente de datos para las conexiones a la base de datos.
 * @property transaction: TransactionManager
 */

class GroupDAO(
    private val console: Console,
    private val dataSource: DataSource,
    private val transaction: TransactionManager
) : IGroupDAO {

    /**
     * Agrega un nuevo grupo a la base de datos.
     * @param grupodesc Descripción del grupo a agregar.
     * @throws SQLException Si ocurre un error al crear el grupo.
     */
    override fun createGroup(grupodesc: String) {
        val sql = "INSERT INTO GRUPOS (grupodesc) VALUES (?);"
        try {
            transaction.openConnection()
            transaction.commit()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, grupodesc)
                stmt.executeUpdate()
            }
            transaction.commit()
            console.showInfo("Grupo creado correctamente.")
        } catch (e: SQLException) {
            transaction.rollback()
            console.showError("Error al crear grupo nuevo: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
    }

    /**
     * Actualiza la descripción del grupo.
     * @param group Entidad del grupo con los datos actualizados.
     * @throws SQLException Si ocurre un error al actualizar el grupo.
     */
    override fun updateGroup(group: GroupEntity) {
        val sql = """
                |UPDATE GRUPOS
                |SET grupodesc =?, mejorposCTFid =?
                |WHERE grupoid =?;
            """.trimMargin()
        try {
            transaction.openConnection()
            transaction.commit()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, group.groupdesc)
                stmt.setInt(2, group.bestpostctfid ?: 0)
                stmt.setInt(3, group.groupid)
                stmt.executeUpdate()
            }
            transaction.commit()
            console.showInfo("Grupo actualizado correctamente.")
        } catch (e: SQLException) {
            transaction.rollback()
            console.showError("Error al actualizar los datos: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
    }

    /**
     * Elimina un grupo de la base de datos.
     * @param groupid ID del grupo a eliminar.
     * @throws SQLException Si ocurre un error al eliminar el grupo.
     */
    override fun deleteGroup(groupid: Int) {
        val sql = """
                |DELETE FROM GRUPOS 
                |WHERE grupoid =? ;
                """.trimIndent()
        try {
            transaction.openConnection()
            transaction.commit()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, groupid)
                stmt.executeUpdate()
            }
            transaction.commit()
            console.showInfo("Grupo eliminado correctamente.")
        } catch (e: SQLException) {
            transaction.rollback()
            console.showError("Error al eliminar el grupo: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
    }

    /**
     * Obtiene la información de un grupo.
     * @param id ID del grupo a obtener.
     * @return La entidad del grupo o null si no se encuentra.
     */
    override fun getGroup(id: Int): GroupEntity? {
        val sql = """
            |SELECT * 
            |FROM GRUPOS
            |WHERE grupoid = ?;
        """.trimMargin()
        try {
            transaction.openConnection()
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    val resultSet = stmt.executeQuery()
                    if (resultSet.next()) {
                        return GroupEntity(
                            groupid = resultSet.getInt("groupid"),
                            groupdesc = resultSet.getString("grupodesc"),
                            bestpostctfid = resultSet.getInt("mejorposCTFid").takeIf { it != 0 }  // Handle null if 0
                        )
                    }
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al obtener el grupo: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
        return null
    }

    /**
     * Obtiene la información de todos los grupos en la tabla 'Grupos'.
     * @return Lista de entidades de los grupos.
     * @throws SQLException Si ocurre un error al obtener los grupos.
     */
    override fun getAllGroups(): List<GroupEntity> {
        val grupos = mutableListOf<GroupEntity>()
        val sql = """
            SELECT * 
            FROM GRUPOS;
            """.trimIndent()
        try {
            transaction.openConnection()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                val result = stmt.executeQuery()
                while (result.next()) {
                    val group = GroupEntity(
                        groupid = result.getInt("grupoid"),
                        groupdesc = result.getString("grupodesc"),
                        bestpostctfid = result.getInt("mejorposCTFid") as? Int
                    )
                    grupos.add(group)
                }
            }
        } catch (e: SQLException) {
            transaction.rollback()
            console.showError("Error al recibir los datos: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
        return grupos
    }

    /**
     * Obtiene la información del grupo que tiene mejor puntuación en su CTF
     * @param groupId Int Número identificativo del grupo
     * @return El grupo con mayor puntuación en su CTF
     * @throws SQLException Si ocurre un error al recibir la información.
     */
    override fun getBestCTF(groupId: Int): CTFEntity? {
        val sql = """
        SELECT CTFid, grupoid, MAX(puntuacion) as puntuacion
        FROM CTFS
        WHERE grupoid = ?
        GROUP BY CTFid, grupoid
        ORDER BY puntuacion DESC
        LIMIT 1;
    """.trimIndent()
        try {
            transaction.openConnection()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, groupId)
                val resultSet = stmt.executeQuery()
                if (resultSet.next()) {
                    return CTFEntity(
                        ctfId = resultSet.getInt("CTFid"),
                        groupid = resultSet.getInt("grupoid"),
                        score = resultSet.getInt("puntuacion")
                    )
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al obtener el mejor CTF: ${e.message}")
            throw e
        } finally {
            transaction.closeConnection()
        }
        return null
    }
}
