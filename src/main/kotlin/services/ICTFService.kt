package services

import entitiesDAO.CTFEntity

interface ICTFService {
    fun addCTF(ctf: CTFEntity)
    fun getCTFById(id: Int): CTFEntity?
    fun updateCTF(ctfId: Int, groupId: Int, newScore: Int)
    fun deleteCTFById(id: Int)
    fun getAllCTFs(): List<CTFEntity>
}