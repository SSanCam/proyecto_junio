package dao

import entitiesDAO.CTFEntity

interface ICTFDAO{
    fun addCTF(ctf: CTFEntity)
    fun getCTFById(id: Int): CTFEntity?
    fun updateCTF(ctfId: Int, groupId: Int, newScore: Int)
    fun deleteCTFById(id: Int)
    fun getAllCTFs(): List<CTFEntity>
}