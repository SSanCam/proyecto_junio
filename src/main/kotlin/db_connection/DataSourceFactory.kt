package db_connection

import org.h2.jdbcx.JdbcDataSource
import javax.sql.DataSource

object DataSourceFactory {
    enum class DataSourceType {
        HIKARI,
        H2,
        JSON,
        XML
    }

    fun getDS(dataSourceType: DataSourceType): DataSource {
        return when (dataSourceType) {
            DataSourceType.H2 -> {
                val dataSource = JdbcDataSource()
                dataSource.setURL("jdbc:h2:http://192.168.18.168:8082/login.jsp?jsessionid=423d698df891dab4abdef2cf2b1b92b0")
                dataSource.user = "user"
                dataSource.password = "user"
                dataSource
            }
            // Resto de tipos de fuentes de datos.
            DataSourceType.HIKARI -> {
                TODO()
            }
            DataSourceType.JSON -> {
                TODO()
            }
            DataSourceType.XML -> {
                val myXMLDataSource = MyXMLDataSource
                val dataSource = myXMLDataSource
                TODO("El resto de implementación de la fuente de datos.")
            }
        }
    }
}
