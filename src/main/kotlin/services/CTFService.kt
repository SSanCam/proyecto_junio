package services

import dao.ICTFDAO
import db_connection.DAOFactory
import entitiesDAO.CTFEntity
import utilities.Console
import java.sql.SQLException

class CTFService(private val console: Console, private val CTFDAO: ICTFDAO) : ICTFService {

    // Agrega un nuevo registro de CTF en la base de datos
    override fun addCTF(ctf: CTFEntity) {
        try {
            val existingCTF = CTFDAO.getCTFById(ctf.CTFid)
            if (existingCTF == null) {
                CTFDAO.addCTF(ctf)
                console.showInfo("Nueva CTF agregado correctamente.")
            } else {
                console.showError("El CTF ya existe.")
            }
        } catch (e: Exception) {
            console.showError("Error al agregar el CTF: ${e.message}")
        }
    }

    // Obtenemos el registro de un CTF por su ID
    override fun getCTFById(id: Int): CTFEntity? {
        return try {
            CTFDAO.getCTFById(id)?.also {
                console.showInfo("CTF obtenido correctamente.")
            }
        } catch (e: Exception) {
            console.showError("Error al obtener el CTF: ${e.message}")
            null
        }
    }

    // Actualizamos un CTF
    override fun updateCTF(ctfId: Int, groupId: Int, newScore: Int) {
        val dataSource = DAOFactory.getDS(DAOFactory.DataSourceType.H2)
        dataSource.connection.use { conn ->
            try {
                conn.autoCommit = false
                val insertSql = """
                    |INSERT INTO CTFS (grupoid, puntuacion)
                    |VALUES (?, ?);
                """.trimIndent()
                conn.prepareStatement(insertSql).use { stmt ->
                    stmt.setInt(1, groupId)
                    stmt.setObject(2, newScore, java.sql.Types.INTEGER)
                    stmt.executeUpdate()
                }
                console.showInfo("Participación de CTF creada o actualizada correctamente.")
            } catch (e: SQLException) {
                conn.rollback()
                console.showError("Error al actualizar la participación en CTF: ${e.message}")
            }
        }
    }

    // Elimina un registro de la tabla CTF
    override fun deleteCTFById(id: Int) {
        try {
            CTFDAO.deleteCTFById(id)
            console.showInfo("Participación de CTF eliminada correctamente.")
        } catch (e: Exception) {
            console.showError("Error al eliminar la participación de CTF: ${e.message}")
        }
    }

    override fun getAllCTFs(): List<CTFEntity> {
        return try {
            CTFDAO.getAllCTFs().also {
                console.showInfo("Lista de todas las participaciones de CTF obtenida correctamente.")
            }
        } catch (e: Exception) {
            console.showError("Error al listar las participaciones de CTF: ${e.message}")
            emptyList()
        }
    }
}