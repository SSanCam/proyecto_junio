package db_connection

import dao.ICTFDAO
import dao.IGroupDAO

abstract class DAOFactory {

    abstract fun getCTFDAO(): ICTFDAO
    abstract fun getGroupDAO(): IGroupDAO

}
