package dao

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
 * @property transactionManager: TransactionManager
 */

class GroupDAO(
    private val console: Console,
    private val dataSource: DataSource,
    private val transactionManager: TransactionManager
) : IGroupDAO {

    /**
     * Agrega un nuevo grupo a la base de datos.
     * @param grupodesc Descripción del grupo a agregar.
     * @throws SQLException Si ocurre un error al crear el grupo.
     */
    override fun createGroup(grupodesc: String) {
        try {
            transactionManager.openConnection()
            transactionManager.commit()
            val conn = transactionManager.getConnection()
            val sql = "INSERT INTO GRUPOS (grupodesc) VALUES (?);"
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, grupodesc)
                stmt.executeUpdate()
            }
            transactionManager.commit()
        } catch (e: SQLException) {
            transactionManager.rollback()
            console.showError("Error al crear grupo nuevo: ${e.message}")
            throw e
        } finally {
            transactionManager.closeConnection()
        }
    }

    /**
     * Actualiza la descripción del grupo.
     * @param group Entidad del grupo con los datos actualizados.
     * @throws SQLException Si ocurre un error al actualizar el grupo.
     */
    override fun updateGroup(group: GroupEntity) {
        try {
            transactionManager.openConnection()
            val sql = """
                |UPDATE GRUPOS
                |SET grupodesc =?, mejorposCTFid =?
                |WHERE grupoid =?;
            """.trimMargin()
            val conn = transactionManager.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, group.grupodesc)
                stmt.setInt(2, group.mejorposCTFid ?: 0)
                stmt.setInt(3, group.groupid)
                stmt.executeUpdate()
                transactionManager.commit()
            }
        } catch (e: SQLException) {
            transactionManager.rollback()
            console.showError("Error al actualizar los datos: ${e.message}")
            throw e
        } finally {
            transactionManager.closeConnection()
        }
    }

    /**
     * Elimina un grupo de la base de datos.
     * @param groupid ID del grupo a eliminar.
     * @throws SQLException Si ocurre un error al eliminar el grupo.
     */
    override fun deleteGroup(groupid: Int) {
        try {
            transactionManager.openConnection()
            val sql = """
                |DELETE FROM GRUPOS 
                |WHERE grupoid =? ;
                """.trimIndent()
            val conn = transactionManager.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, groupid)
                stmt.executeUpdate()
            }
            transactionManager.commit()
        } catch (e: SQLException) {
            transactionManager.rollback()
            console.showError("Error al eliminar el grupo: ${e.message}")
            throw e
        } finally {
            transactionManager.closeConnection()
        }
    }

    /**
     * Obtiene la información de un grupo.
     * @param id ID del grupo a obtener.
     * @return La entidad del grupo o null si no se encuentra.
     */
    override fun getGroup(id: Int): GroupEntity? {
        try {
            val sql = """
            |SELECT * 
            |FROM GRUPOS
            |WHERE grupoid = ?;
        """.trimMargin()
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, id)
                    val resultSet = stmt.executeQuery()
                    if (resultSet.next()) {
                        return GroupEntity(
                            groupid = resultSet.getInt("groupid"),
                            grupodesc = resultSet.getString("grupodesc"),
                            mejorposCTFid = resultSet.getInt("mejorposCTFid").takeIf { it != 0 }  // Handle null if 0
                        )
                    }
                }
            }
        } catch (e: SQLException) {
            transactionManager.rollback()
            console.showError("Error al obtener el grupo: ${e.message}")
            throw e
        } finally {
            transactionManager.closeConnection()
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
        try {
            val sql = """
            SELECT * 
            FROM GRUPOS;
            """.trimIndent()

            transactionManager.openConnection()
            val conn = transactionManager.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                val result = stmt.executeQuery()
                while (result.next()) {
                    val group = GroupEntity(
                        groupid = result.getInt("grupoid"),
                        grupodesc = result.getString("grupodesc"),
                        mejorposCTFid = result.getInt("mejorposCTFid") as? Int
                    )
                    grupos.add(group)
                }
            }
        } catch (e: SQLException) {
            transactionManager.rollback()
            console.showError("Error al recibir los datos: ${e.message}")
            throw e
        } finally {
            transactionManager.closeConnection()
        }
        return grupos
    }
}