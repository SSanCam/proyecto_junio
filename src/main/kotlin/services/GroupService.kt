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

    /**
     * Crea un nuevo grupo en la base de datos.
     * @param groupdesc Descripción del grupo.
     * @throws SQLException Si ocurre un error al crear el grupo.
     */
    override fun createGroup(groupdesc: String) {
        try {
            groupDAO.createGroup(groupdesc)
            console.showInfo("Nuevo grupo creado con éxito.")
        } catch (e: SQLException) {
            console.showError("Error al crear nuevo grupo: ${e.message}")
        }
    }

    /**
     * Actualiza la información de un grupo en la base de datos.
     * @param group Entidad del grupo con los datos actualizados.
     * @throws SQLException Si ocurre un error al actualizar el grupo.
     */
    override fun updateGroup(group: GroupEntity) {
        try {
            groupDAO.updateGroup(group)
            console.showInfo("Grupo actualizado con éxito.")
        } catch (e: SQLException) {
            console.showError("Error al actualizar el grupo: ${e.message}")
        }
    }

    /**
     * Elimina un grupo de la base de datos.
     * @param groupID ID del grupo a eliminar.
     * @throws SQLException Si ocurre un error al eliminar el grupo.
     */
    override fun deleteGroup(groupID: Int) {
        try {
            groupDAO.deleteGroup(groupID)
            console.showInfo("Grupo eliminado con éxito.")
        } catch (e: SQLException) {
            console.showError("Error al eliminar el grupo.")
        }
    }

    /**
     * Obtiene la información de un grupo por su ID.
     * @param groupID ID del grupo a obtener.
     * @return La entidad del grupo o null si no se encuentra.
     * @throws SQLException Si ocurre un error al obtener el grupo.
     */
    override fun getGroupByID(groupID: Int): GroupEntity? {
        try {
            return groupDAO.getGroup(groupID)
        } catch (e: SQLException) {
            console.showError("Error al obtener el grupo: ${e.message}")
        }
        return null
    }

    /**
     * Obtiene la información de todos los grupos en la base de datos.
     * @return Lista de entidades de los grupos.
     * @throws SQLException Si ocurre un error al obtener los grupos.
     */
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

    /**
     * Actualiza el mejor CTF de un grupo.
     * @param groupId ID del grupo para actualizar su mejor CTF.
     * @throws SQLException Si ocurre un error al actualizar el mejor CTF.
     */
    fun updateBestCTF(groupId: Int) {
        try {
            val group = getGroupByID(groupId) ?: return
            val bestCTF = groupDAO.getBestCTF(groupId)
            groupDAO.updateGroup(group.copy(bestpostctfid = bestCTF?.ctfId))
            console.showInfo("Actualización de mejor CTF completada.")
        } catch (e: SQLException) {
            console.showError("Error al actualizar el mejor CTF: ${e.message}")
        }
    }
}
