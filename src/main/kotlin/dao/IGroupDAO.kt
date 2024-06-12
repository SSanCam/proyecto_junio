package dao

import entitiesDAO.CTFEntity
import entitiesDAO.GroupEntity

interface IGroupDAO {
    fun createGroup(grupodesc: String)
    fun updateGroup(group: GroupEntity)
    fun deleteGroup(groupid: Int)
    fun getGroup(id: Int): GroupEntity?
    fun getAllGroups(): List<GroupEntity>
    fun getBestCTF(groupId: Int): CTFEntity?
}