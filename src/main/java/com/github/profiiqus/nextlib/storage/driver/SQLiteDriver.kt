package com.github.profiiqus.nextlib.storage.driver

import com.github.profiiqus.nextlib.storage.SQLCallback
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class SQLiteDriver(plugin: JavaPlugin) : SQLDriver(plugin) {

    private val databaseFile: File

    init {
        this.databaseFile = File(plugin.dataFolder, super.settings.localFileName + ".db")
    }

    override fun setup() {
        if(!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }

        if(!databaseFile.exists()) {
            databaseFile.createNewFile()
        }
    }

    override fun createConnection(): Connection {
        if(connection != null && !connection!!.isClosed) {
            return connection!!
        }

        Class.forName("org.sqlite.SQLiteDataSource")
        connection = DriverManager.getConnection("jdbc:sqlite:${databaseFile.path}")
        return connection!!
    }

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