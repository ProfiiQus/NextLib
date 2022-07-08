package com.github.profiiqus.nextlib.storage.driver

import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection

/**
 * SQL Driver for communication with the MySQL database.
 * @author ProfiiQus
 */
class MySQLDriver(plugin: JavaPlugin) : SQLDriver(plugin) {

    private var dataSource: HikariDataSource? = null

    /**
     * Setups the database driver.
     * Configures the Hikari connection pool, loads the SQL driver and instantiates the data source.
     */
    override fun setup() {
        super.setupHikariConfig()
        Class.forName("com.mysql.jdbc.Driver")
        super.setJDBCUrl("jdbc:mysql")
        this.dataSource = HikariDataSource(super.hikariConfig)
    }

    /**
     * Returns an established connection to the database.
     * The connection can be either newly opened or an already opened previous connection.
     */
    override fun createConnection(): Connection {
        if (connection != null && !connection!!.isClosed) {
            return connection as Connection
        }

        connection = dataSource!!.connection
        return connection as Connection
    }

}