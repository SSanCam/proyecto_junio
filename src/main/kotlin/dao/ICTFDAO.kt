package dao

import entitiesDAO.CTFEntity

interface ICTFDAO{
    fun createCTF(ctfEntity: CTFEntity)
    fun getCTFById(id: Int): CTFEntity?
    fun updateCTF(ctfId: Int, groupId: Int, newScore: Int)
    fun deleteCTFById(ctfId: Int)
    fun getAllCTFs(): List<CTFEntity>
}