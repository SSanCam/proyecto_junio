package db_connection

import dao.ICTFDAO
import dao.IGroupDAO
import utilities.Console
import javax.sql.DataSource

abstract class DAOFactory {

    abstract fun getCTFDAO(): ICTFDAO
    abstract fun getGroupDAO(): IGroupDAO

    companion object {
        fun getFactory(dataSourceType: DataSourceType, dataSource: DataSource, console: Console): DAOFactory {
            return when (dataSourceType) {
                DataSourceType.H2 -> db_connection.SQLDAOFactory(dataSource, console)
                // Otros tipos de fuentes de datos pueden ser añadidos aquí.

                DataSourceType.XML -> {
                    TODO()
                }

                DataSourceType.JSON -> {
                    TODO()
                }

                DataSourceType.HIKARI -> {
                    TODO()
                }
            }
        }
    }

}
