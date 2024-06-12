package services

import entitiesDAO.CTFEntity

interface ICTFService {
    fun createCTF(groupId: Int, score: Int)
    fun updateCTF(ctfId: Int, groupId: Int, newScore: Int)
    fun deleteCTFById(ctfId: Int)
    fun getCTFById(ctfId: Int): CTFEntity?
    fun getAllCTFs(): List<CTFEntity>
}
