package services

import entitiesDAO.GroupEntity

interface IGroupService {
    fun createGroup(groupdesc: String)
    fun updateGroup(group: GroupEntity)
    fun deleteGroup(groupID: Int)
    fun getGroupByID(groupID: Int): GroupEntity?
    fun getAllGroups(): List<GroupEntity>
}