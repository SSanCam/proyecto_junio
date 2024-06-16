package services

import dao.ICTFDAO
import dao.IGroupDAO
import entitiesDAO.CTFEntity
import utilities.Console
import utilities.ExportFileManagement
import java.sql.SQLException

/**
 * Servicio para la gestión de CTFs que encapsula la lógica de negocio y utiliza ICTFDAO para interactuar con la base de datos.
 *
 * @property console Consola para mostrar información y errores.
 * @property ctfDAO DAO para las operaciones de base de datos relacionadas con los CTFs.
 * @property groupDAO DAO para las operaciones de base de datos relacionadas con los grupos.
 */
class CTFService(
    private val console: Console,
    private val ctfDAO: ICTFDAO,
    private val groupDAO: IGroupDAO
) : ICTFService {

    /**
     * Crea un nuevo registro en CTFS.
     * @param groupId Int Indica el número de identificación del grupo asociado al nuevo CTF.
     * @param score Int Indica la puntuación del CTF.
     * @throws SQLException En caso de no poder crear un nuevo registro.
     */
    override fun createCTF(groupId: Int, score: Int) {
        try {
            // Verificar si el grupo existe
            val group = groupDAO.getGroup(groupId)
            if (group == null) {
                console.showError("El grupo con ID $groupId no existe.")
                return
            }

            ctfDAO.createCTF(CTFEntity(groupid = groupId, score = score))
            console.showInfo("Nuevo CTF creado correctamente.")
        } catch (e: SQLException) {
            console.showError("Error al crear el CTF: ${e.message}")
            throw e
        }
    }

    /**
     * Actualiza un registro de CTFS.
     * @param ctfId Int Número identificativo del CTF.
     * @param groupId Int Número identificativo del grupo.
     * @param newScore Int Puntuación del CTF.
     * @throws SQLException Error en caso de no poder actualizar la información.
     */
    override fun updateCTF(ctfId: Int, groupId: Int, newScore: Int) {
        try {
            // Verificar si el grupo existe
            val group = groupDAO.getGroup(groupId)
            if (group == null) {
                console.showError("El grupo con ID $groupId no existe.")
                return
            }

            ctfDAO.updateCTF(ctfId, groupId, newScore)
            console.showInfo("Actualización de CTF realizada correctamente.")
        } catch (e: SQLException) {
            console.showError("Error al actualizar el CTF: ${e.message}")
            throw e
        }
    }

    /**
     * Elimina un registro de CTFS.
     * @param ctfId Int El número identificativo del CTF a eliminar.
     * @throws SQLException Mensaje de error en caso de no poder llevarse a cabo la eliminación.
     */
    override fun deleteCTFById(ctfId: Int) {
        try {
            ctfDAO.deleteCTFById(ctfId)
            console.showInfo("CTF eliminado correctamente.")
        } catch (e: SQLException) {
            console.showError("Error al eliminar la participación: ${e.message}")
        }
    }

    /**
     * Obtenemos un registro completo de un CTF según su ID.
     * @param ctfId Int Número identificativo del CTF del que queremos obtener la información.
     * @return Información del CTF que se busca.
     * @throws SQLException Mensaje de error al obtener la información.
     */
    override fun getCTFById(ctfId: Int): CTFEntity? {
        return try {
            ctfDAO.getCTFById(ctfId).also {
                console.showInfo("Datos CTF obtenido correctamente.")
            }
        } catch (e: SQLException) {
            console.showError("Error al recibir los datos CTFS: ${e.message}")
            throw e
        }
    }

    /**
     * Obtenemos una lista con todos los CTF registrados.
     * @return Lista con todos los CTF registrados.
     * @throws SQLException Mensaje de error al recibir los datos.
     */
    override fun getAllCTFs(): List<CTFEntity> {
        return try {
            ctfDAO.getAllCTFs().also {
                console.showInfo("Lista de todas las participaciones obtenida correctamente.")
            }
        } catch (e: SQLException) {
            console.showError("Error al listar las participaciones: ${e.message}")
            emptyList()
        }
    }

    /**
     * Exporta la clasificación de CTFs a un fichero.
     * @param groupService El servicio de grupo para obtener los nombres de los grupos.
     * @param filename El nombre del fichero donde se exportará la clasificación.
     */
    override fun exportCTFClassification(groupService: GroupService, filename: String) {
        try {
            val allCTFs = ctfDAO.getAllCTFs()
            val groupedCTFs = allCTFs.groupBy { it.ctfId }

            val exportManager = ExportFileManagement(console, filename)
            exportManager.clearFile()

            groupedCTFs.forEach { (ctfId, ctfList) ->
                exportManager.writeToFile("CTF: $ctfId\n")
                ctfList.sortedByDescending { it.score }.forEachIndexed { index, ctf ->
                    val groupName = groupService.getGroupByID(ctf.groupid)?.groupdesc ?: "N/A"
                    exportManager.writeToFile("${index + 1}. $groupName (${ctf.score} puntos)\n")
                }
                exportManager.writeToFile("\n")
            }
            console.showInfo("Clasificación exportada correctamente a $filename")
        } catch (e: SQLException) {
            console.showError("Error al exportar la clasificación: ${e.message}")
        } catch (e: Exception) {
            console.showError("Error inesperado al exportar la clasificación: ${e.message}")
        }
    }
}
