package dao

import entitiesDAO.CTFEntity
import entitiesDAO.GroupEntity
import utilities.Console
import utilities.TransactionManager
import java.sql.SQLException
import javax.sql.DataSource

class GroupDAO(
    private val console: Console,
    private val dataSource: DataSource,
    private val transaction: TransactionManager
) : IGroupDAO {

    override fun createGroup(grupodesc: String) {
        val sql = "INSERT INTO GRUPOS (GRUPODESC) VALUES (?);"
        try {
            transaction.openConnection()
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

    override fun updateGroup(group: GroupEntity) {
        val sql = "UPDATE GRUPOS SET GRUPODESC = ?, MEJORPOSCTFID = ? WHERE GRUPOID = ?;"
        try {
            transaction.openConnection()
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

    override fun deleteGroup(groupid: Int) {
        val sql = "DELETE FROM GRUPOS WHERE GRUPOID = ?;"
        try {
            transaction.openConnection()
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

    override fun getGroup(id: Int): GroupEntity? {
        val sql = "SELECT * FROM GRUPOS WHERE GRUPOID = ?;"
        try {
            transaction.openConnection()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val resultSet = stmt.executeQuery()
                if (resultSet.next()) {
                    return GroupEntity(
                        groupid = resultSet.getInt("GRUPOID"),
                        groupdesc = resultSet.getString("GRUPODESC"),
                        bestpostctfid = resultSet.getInt("MEJORPOSCTFID").takeIf { it != 0 }
                    )
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

    override fun getAllGroups(): List<GroupEntity> {
        val grupos = mutableListOf<GroupEntity>()
        val sql = "SELECT * FROM GRUPOS;"
        try {
            transaction.openConnection()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                val result = stmt.executeQuery()
                while (result.next()) {
                    val group = GroupEntity(
                        groupid = result.getInt("GRUPOID"),
                        groupdesc = result.getString("GRUPODESC"),
                        bestpostctfid = result.getInt("MEJORPOSCTFID").takeIf { it != 0 }
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

    override fun getBestCTF(groupId: Int): CTFEntity? {
        val sql = "SELECT CTFID, GRUPOID, MAX(PUNTUACION) as PUNTUACION FROM CTFS WHERE GRUPOID = ? GROUP BY CTFID, GRUPOID ORDER BY PUNTUACION DESC LIMIT 1;"
        try {
            transaction.openConnection()
            val conn = transaction.getConnection()
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, groupId)
                val resultSet = stmt.executeQuery()
                if (resultSet.next()) {
                    return CTFEntity(
                        ctfId = resultSet.getInt("CTFID"),
                        groupid = resultSet.getInt("GRUPOID"),
                        score = resultSet.getInt("PUNTUACION")
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
