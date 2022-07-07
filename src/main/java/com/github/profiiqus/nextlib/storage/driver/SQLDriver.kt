package com.github.profiiqus.nextlib.storage.driver

import com.github.profiiqus.nextlib.storage.SQLCallback
import com.github.profiiqus.nextlib.storage.StorageSettings
import com.zaxxer.hikari.HikariConfig
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

abstract class SQLDriver(protected var plugin: JavaPlugin) {

    protected var connection: Connection? = null
    protected var hikariConfig: HikariConfig? = null
    var settings: StorageSettings = StorageSettings()

    companion object {
        private const val TEST_QUERY = "SELECT 1"
    }

    open fun setup() {
        throw NotImplementedException()
    }

    open fun createConnection(): Connection {
        throw NotImplementedException()
    }

    open fun execute(query: String?) {
        object : BukkitRunnable() {
            override fun run() {
                connection = createConnection()
                val statement = connection!!.createStatement()
                statement.execute(query)
                statement.close()
                connection!!.close()
            }
        }.runTaskAsynchronously(plugin)
    }

    open fun execute(preparedStatement: PreparedStatement) {
        object: BukkitRunnable() {
            override fun run() {
                connection = createConnection()
                preparedStatement.execute()
                preparedStatement.close()
                connection!!.close()
            }
        }.runTaskAsynchronously(plugin)
    }

    open fun executeQuery(query: String?, callback: SQLCallback) {
        object : BukkitRunnable() {
            override fun run() {
                connection = createConnection()
                val statement = connection!!.createStatement()
                val resultSet = statement.executeQuery(query)
                callback.onQueryDone(resultSet)
                connection!!.close()
                statement.close()
                resultSet.close()
            }
        }.runTaskAsynchronously(plugin)
    }

    open fun executeQuery(preparedStatement: PreparedStatement, callback: SQLCallback) {
        object: BukkitRunnable() {
            override fun run() {
                connection = createConnection()
                val resultSet = preparedStatement.executeQuery()
                callback.onQueryDone(resultSet)
                connection!!.close()
                preparedStatement.close()
                resultSet.close()
            }
        }.runTaskAsynchronously(plugin)
    }

    fun exit() {
        if (connection != null && !connection!!.isClosed) {
            connection!!.close()
        }
    }

    @Throws(SQLException::class, ClassNotFoundException::class)
    fun test() {
        val statement = createConnection().createStatement()
        statement.execute(TEST_QUERY)
        statement.close()
    }

    protected fun setupHikariConfig() {
        hikariConfig = HikariConfig()
        val properties = hikariConfig!!.dataSourceProperties
        properties.setProperty("serverName", settings.hostname)
        properties.setProperty("port", settings.port.toString())
        properties.setProperty("databaseName", settings.database)
        properties.setProperty("user", settings.username)
        properties.setProperty("password", settings.password)
        hikariConfig!!.dataSourceProperties = properties
        for ((key, value) in settings.dataSourceProperties) {
            hikariConfig!!.addDataSourceProperty(key, value)
        }
    }

    protected fun setJDBCUrl(driverPath: String) {
        val properties = this.hikariConfig!!.dataSourceProperties
        this.hikariConfig!!.jdbcUrl = "$driverPath://${properties["serverName"]}:${properties["port"]}/${properties["databaseName"]}"
        plugin.logger.info("JDBC URL: ${this.hikariConfig!!.jdbcUrl}")
    }

}
