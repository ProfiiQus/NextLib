package com.github.profiiqus.nextlib.storage.driver

import com.github.profiiqus.nextlib.storage.SQLCallback
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * SQL Driver for communication with the SQLite database.
 * @author ProfiiQus
 */
class SQLiteDriver(plugin: JavaPlugin) : SQLDriver(plugin) {

    private val databaseFile: File

    init {
        this.databaseFile = File(plugin.dataFolder, super.settings.localFileName + ".db")
    }

    /**
     * Setups the database driver.
     * If the plugin's folder does not exist, creates it.
     * If the database's file does not exist, creates it.
     */
    override fun setup() {
        if(!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }

        if(!databaseFile.exists()) {
            databaseFile.createNewFile()
        }
    }

    /**
     * Returns an established connection to the database.
     * The connection can be either newly opened or an already opened previous connection.
     */
    override fun createConnection(): Connection {
        if(connection != null && !connection!!.isClosed) {
            return connection!!
        }

        Class.forName("org.sqlite.SQLiteDataSource")
        connection = DriverManager.getConnection("jdbc:sqlite:${databaseFile.path}")
        return connection!!
    }

    /**
     * Asynchronously executes an SQL query on the database.
     */
    override fun execute(query: String?) {
        object : BukkitRunnable() {
            override fun run() {
                connection = createConnection()
                val statement = connection!!.createStatement()
                statement.execute(query)
                statement.close()
            }
        }.runTaskAsynchronously(plugin)
    }

    /**
     * Asynchronously executes an SQL query on the database.
     * Result of the SQL query is returned inside the SQLCallback.
     */
    override fun executeQuery(query: String?, callback: SQLCallback) {
        object : BukkitRunnable() {
            override fun run() {
                connection = createConnection()
                val statement = connection!!.createStatement()
                val resultSet = statement.executeQuery(query)
                callback.onQueryDone(resultSet)
                statement.close()
                resultSet.close()
            }
        }.runTaskAsynchronously(plugin)
    }

}