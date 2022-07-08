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

/**
 * An abstract class for SQLDriver creation.
 * @author ProfiiQus
 */
abstract class SQLDriver(protected var plugin: JavaPlugin) {

    protected var connection: Connection? = null
    protected var hikariConfig: HikariConfig? = null
    var settings: StorageSettings = StorageSettings()

    companion object {
        private const val TEST_QUERY = "SELECT 1"
    }

    /**
     * Prepares and configures the SQLDriver.
     */
    open fun setup() {
        throw NotImplementedException()
    }

    /**
     * Returns an open connection to the database.
     */
    open fun createConnection(): Connection {
        throw NotImplementedException()
    }

    /**
     * Executes an asynchronous query on the database.
     */
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

    /**
     * Executes a synchronous query on the database.
     */
    open fun executeSync(query: String?) {
        connection = createConnection()
        val statement = connection!!.createStatement()
        statement.execute(query)
        statement.close()
        connection!!.close()
    }

    /**
     * Executes an asynchronous prepared statement query on the database.
     */
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

    /**
     * Executes a synchronous prepared statement query on the database.
     */
    open fun executeSync(preparedStatement: PreparedStatement) {
        connection = createConnection()
        preparedStatement.execute()
        preparedStatement.close()
        connection!!.close()
    }

    /**
     * Executes an asynchronous query on the database.
     * The result of the query is returned to the provided SQLCallback once the query is completed.
     */
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

    /**
     * Executes a synchronous query on the database.
     * The result of the query is returned to the provided SQLCallback once the query is completed.
     */
    open fun executeSyncQuery(query: String?, callback: SQLCallback) {
        connection = createConnection()
        val statement = connection!!.createStatement()
        val resultSet = statement.executeQuery(query)
        callback.onQueryDone(resultSet)
        connection!!.close()
        statement.close()
        resultSet.close()
    }

    /**
     * Executes an asynchronous prepared statement query on the database.
     * The result of the query is returned to the provided SQLCallback once the query is completed.
     */
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

    /**
     * Executes a synchronous prepared statement query on the database.
     * The result of the query is returned to the provided SQLCallback once the query is completed.
     */
    open fun executeSyncQuery(preparedStatement: PreparedStatement, callback: SQLCallback) {
        connection = createConnection()
        val resultSet = preparedStatement.executeQuery()
        callback.onQueryDone(resultSet)
        connection!!.close()
        preparedStatement.close()
        resultSet.close()
    }

    /**
     * Closes all the connections and disables the SQL Driver.
     */
    fun exit() {
        if (connection != null && !connection!!.isClosed) {
            connection!!.close()
        }
    }

    /**
     * Tests the connection to the database.
     */
    @Throws(SQLException::class, ClassNotFoundException::class)
    fun test() {
        val statement = createConnection().createStatement()
        statement.execute(TEST_QUERY)
        statement.close()
    }

    /**
     * Configures the Hikari connection pool instance from the provided StorageSettings.
     */
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

    /**
     * Builds the JDBC url.
     */
    protected fun setJDBCUrl(driverPath: String) {
        val properties = this.hikariConfig!!.dataSourceProperties
        this.hikariConfig!!.jdbcUrl = "$driverPath://${properties["serverName"]}:${properties["port"]}/${properties["databaseName"]}"
        plugin.logger.info("JDBC URL: ${this.hikariConfig!!.jdbcUrl}")
    }

}
