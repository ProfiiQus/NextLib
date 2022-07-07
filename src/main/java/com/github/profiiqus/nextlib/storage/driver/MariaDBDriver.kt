package com.github.profiiqus.nextlib.storage.driver

import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection

class MariaDBDriver(plugin: JavaPlugin) : SQLDriver(plugin) {

    private var dataSource: HikariDataSource? = null

    override fun setup() {
        super.setupHikariConfig()
        Class.forName("org.mariadb.jdbc.Driver")
        super.setJDBCUrl("jdbc:mariadb")
        dataSource = HikariDataSource(super.hikariConfig)
    }

    override fun createConnection(): Connection {
        if (connection != null && !connection!!.isClosed) {
            return connection as Connection
        }

        connection = dataSource!!.connection
        return connection as Connection
    }

}