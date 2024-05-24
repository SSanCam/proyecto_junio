package dao

import entitiesDAO.GroupEntity
import utilities.Console
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Clase que implementa la interfaz IGroupDAO para manejar operaciones de la base de datos
 * relacionadas con los grupos.
 *
 * @property console Instancia de Console para mostrar mensajes de error.
 * @property dataSource Fuente de datos para las conexiones a la base de datos.
 */
class GroupDAO(private val console: Console, private val dataSource: DataSource) : IGroupDAO {

    // Agrega un nuevo grupo a la base de datos
    override fun createGroup(grupodesc: String) {
        try {
            val sql = "INSERT INTO GRUPOS (grupodesc) VALUES (?);"
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, grupodesc)
                    stmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al crear grupo nuevo: ${e.message}")
            throw e
        }
    }


    // Actualiza la descripción del grupo.
    override fun updateGroup(group: GroupEntity) {
        try {
            val sql = """
                |UPDATE GRUPOS
                |SET grupodesc =?, mejorposCTFid =?
                |WHERE grupoid =?;
            """.trimMargin()
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, group.grupodesc)
                    stmt.setInt(2, group.mejorposCTFid ?: 0)
                    stmt.setInt(3, group.groupid)
                    stmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al actualizar los datos: ${e.message}")
            throw e
        }
    }

    // Elimina un grupo de la base de datos.
    override fun deleteGroup(groupid: Int) {
        try {
            val sql = """
                |DELETE FROM GRUPOS 
                |WHERE grupoid =? ;
                """.trimIndent()
            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, groupid)
                    stmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            console.showError("Error al eliminar el grupo: ${e.message}")
            throw e
        }

    }

    // Obtiene la información de un grupo.
    override fun getGroup(id: Int): GroupEntity? {
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
        return null
    }

    // Obtiene la información de todos los grupos en la tabla 'Grupos'
    override fun getAllGroups(): List<GroupEntity> {
        val grupos = mutableListOf<GroupEntity>()
        val sql = """
            SELECT * 
            FROM GRUPOS;
            """.trimIndent()
        try {
            dataSource.connection.use { conn ->
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
            }
        } catch (e: SQLException) {
            console.showError("Error al recibir los datos: ${e.message}")
            throw e
        }
        return grupos
    }

}