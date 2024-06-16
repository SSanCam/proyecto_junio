package services

import dao.IGroupDAO
import entitiesDAO.GroupEntity
import utilities.Console
import java.sql.SQLException

class GroupService(private val console: Console, private val groupDAO: IGroupDAO) {

    fun createGroup(description: String) {
        try {
            groupDAO.createGroup(description)
            console.showInfo("Grupo creado correctamente.")
        } catch (e: SQLException) {
            console.showError("Error al crear grupo: ${e.message}")
        }
    }

    fun updateBestCTF(groupId: Int) {
        try {
            val bestCTF = groupDAO.getBestCTF(groupId)
            val group = groupDAO.getGroup(groupId)
            if (group != null) {
                group.bestpostctfid = bestCTF?.ctfId
                groupDAO.updateGroup(group)
                console.showInfo("Grupo actualizado correctamente.")
            } else {
                console.showError("Grupo no encontrado.")
            }
        } catch (e: SQLException) {
            console.showError("Error al actualizar el grupo: ${e.message}")
        }
    }

    fun deleteGroup(groupId: Int) {
        try {
            groupDAO.deleteGroup(groupId)
            console.showInfo("Grupo eliminado correctamente.")
        } catch (e: SQLException) {
            console.showError("Error al eliminar el grupo: ${e.message}")
        }
    }

    fun getGroupByID(groupId: Int): GroupEntity? {
        return try {
            groupDAO.getGroup(groupId)
        } catch (e: SQLException) {
            console.showError("Error al obtener el grupo: ${e.message}")
            null
        }
    }

    fun getAllGroups(): List<GroupEntity> {
        return try {
            groupDAO.getAllGroups()
        } catch (e: SQLException) {
            console.showError("Error al obtener los grupos: ${e.message}")
            emptyList()
        }
    }
}
