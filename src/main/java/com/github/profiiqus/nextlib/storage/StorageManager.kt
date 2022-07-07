package com.github.profiiqus.nextlib.storage

import com.github.profiiqus.nextlib.storage.driver.MariaDBDriver
import com.github.profiiqus.nextlib.storage.driver.MySQLDriver
import com.github.profiiqus.nextlib.storage.driver.SQLDriver
import com.github.profiiqus.nextlib.storage.driver.SQLiteDriver
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.PreparedStatement

class StorageManager {

    private var plugin: JavaPlugin
    private var settings: StorageSettings
    private var drivers: Map<StorageType, SQLDriver>
    private var driver: SQLDriver

    constructor(plugin: JavaPlugin) : this(plugin, StorageSettings())

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
        driver.settings = settings
        driver.setup()
        driver.test()
    }

    fun getConnection(): Connection {
        return driver.createConnection()
    }

    fun prepareStatement(query: String): PreparedStatement {
        return this.getConnection().prepareStatement(query)
    }

    fun execute(query: String) {
        driver.execute(query)
    }

    fun execute(preparedStatement: PreparedStatement) {
        driver.execute(preparedStatement)
    }

    fun executeQuery(query: String, callback: SQLCallback) {
        driver.executeQuery(query, callback)
    }

    fun executeQuery(preparedStatement: PreparedStatement, callback: SQLCallback) {
        driver.executeQuery(preparedStatement, callback)
    }

    fun exit() {
        driver.exit()
    }

}