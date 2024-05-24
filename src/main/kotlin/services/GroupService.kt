package services

import dao.IGroupDAO
import entitiesDAO.GroupEntity
import utilities.Console
import java.sql.SQLException

/**
 * Servicio para la gestión de grupos que encapsula la lógica de negocio y utiliza IGroupDAO para interactuar con la base de datos.
 *
 * @property console Consola para mostrar información y errores.
 * @property groupDAO DAO para las operaciones de base de datos relacionadas con grupos.
 */

class GroupService(private val console: Console, private val groupDAO: IGroupDAO) : IGroupService {
    override fun createGroup(groupdesc: String) {
        try {
            groupDAO.createGroup(groupdesc)
            console.showInfo("Nuevo grupo creado con éxito.")
        } catch (e: SQLException) {
            console.showError("Error al crear nuevo grupo: ${e.message}")
        }
    }

    override fun updateGroup(group: GroupEntity) {
        try {
            groupDAO.updateGroup(group)
            console.showInfo("Grupo actualizado con éxito.")
        } catch (e: SQLException) {
            console.showError("Error al actualizar el grupo: ${e.message}")
        }
    }

    override fun deleteGroup(groupID: Int) {
        try {
            groupDAO.deleteGroup(groupID)
            console.showInfo("Grupo eliminado con éxito.")
        } catch (e: SQLException) {
            console.showError("Error al eliminar el grupo.")
        }
    }

    override fun getGroupByID(groupID: Int): GroupEntity? {
        try {
            return groupDAO.getGroup(groupID)
        } catch (e: SQLException) {
            console.showError("Error al obtener el grupo: ${e.message}")
        }
        return null
    }

    override fun getAllGroups(): List<GroupEntity> {
        try {
            return groupDAO.getAllGroups().also {
                console.showInfo("Lista de todos los grupos obtenida con éxito.")
            }
        } catch (e: SQLException) {
            console.showError("Error al listar todos los grupos: ${e.message}")
            return emptyList()
        }
    }
}