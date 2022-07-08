package com.github.profiiqus.nextlib.storage

import com.github.profiiqus.nextlib.storage.driver.MariaDBDriver
import com.github.profiiqus.nextlib.storage.driver.MySQLDriver
import com.github.profiiqus.nextlib.storage.driver.SQLDriver
import com.github.profiiqus.nextlib.storage.driver.SQLiteDriver
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.PreparedStatement

/**
 * The main class for communication with the SQL database(s).
 * @author ProfiiQus
 */
class StorageManager {

    private var plugin: JavaPlugin
    private var settings: StorageSettings
    private var drivers: Map<StorageType, SQLDriver>
    private var driver: SQLDriver

    /**
     * Default constructor with default values, initiates a simple SQLite database.
     */
    constructor(plugin: JavaPlugin) : this(plugin, StorageSettings())

    /**
     * A more complex constructor with provided configuration object.
     */
    constructor(plugin: JavaPlugin, settings: StorageSettings) {
        this.plugin = plugin
        this.settings = settings
        this.driver = SQLiteDriver(plugin)

        this.drivers = object : HashMap<StorageType, SQLDriver>() {
            init {
                put(StorageType.SQLITE, SQLiteDriver(plugin))
                put(StorageType.MYSQL, MySQLDriver(plugin))
                put(StorageType.MARIADB, MariaDBDriver(plugin))
            }
        }

        driver = drivers[this.settings.storageType]!!
        driver.setup()
        driver.test()
    }

    /**
     * Returns an open connection to the database.
     */
    fun getConnection(): Connection {
        return driver.createConnection()
    }

    /**
     * Prepares a new statement for usage above the database.
     */
    fun prepareStatement(query: String): PreparedStatement {
        return this.getConnection().prepareStatement(query)
    }

    /**
     * Executes an asynchronous query on the database.
     */
    fun execute(query: String) {
        driver.execute(query)
    }

    /**
     * Executes a synchronous query on the database.
     */
    fun executeSync(query: String) {
        driver.executeSync(query)
    }

    /**
     * Executes an asynchronous prepared statement query on the database.
     */
    fun execute(preparedStatement: PreparedStatement) {
        driver.execute(preparedStatement)
    }

    /**
     * Executes a synchronous prepared statement query on the database.
     */
    fun executeSync(preparedStatement: PreparedStatement) {
        driver.executeSync(preparedStatement)
    }

    /**
     * Executes an asynchronous query on the database.
     * The result of the query is returned as a part of the SQLCallback.
     */
    fun executeQuery(query: String, callback: SQLCallback) {
        driver.executeQuery(query, callback)
    }

    /**
     * Executes a synchronous query on the database.
     * The result of the query is returned as a part of the SQLCallback.
     */
    fun executeSyncQuery(query: String, callback: SQLCallback) {
        driver.executeSyncQuery(query, callback)
    }

    /**
     * Executes an asynchronous prepared statement query on the database.
     * The result of the query is returned as a part of the SQLCallback.
     */
    fun executeQuery(preparedStatement: PreparedStatement, callback: SQLCallback) {
        driver.executeQuery(preparedStatement, callback)
    }

    /**
     * Executes a synchronous prepared statement query on the database.
     * The result of the query is returned as a part of the SQLCallback.
     */
    fun executeSyncQuery(preparedStatement: PreparedStatement, callback: SQLCallback) {
        driver.executeSyncQuery(preparedStatement, callback)
    }

    /**
     * Closes all the connections and exits the drivers.
     * Use only when disabling the plugin.
     */
    fun exit() {
        driver.exit()
    }

}