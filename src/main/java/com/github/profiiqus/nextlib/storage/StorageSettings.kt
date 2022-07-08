package com.github.profiiqus.nextlib.storage

/**
 * Simple wrapper class representing the StorageManager's complete configuration.
 * @author ProfiiQus
 */
class StorageSettings private constructor(
    val storageType: StorageType,
    val hostname: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val localFileName: String,
    val dataSourceProperties: HashMap<String, String>
) {

    /**
     * Default constructor with default values, configured for SQLite.
     */
    constructor() : this(
        StorageType.SQLITE,
        "",
        3306,
        "",
        "",
        "",
        "",
        hashMapOf()
    )

    /**
     * Constructor with values specified.
     */
    constructor(
        storageType: StorageType,
        hostname: String,
        port: Int,
        database: String,
        username: String,
        password: String
    ) : this(storageType, hostname, port, database, username, password, "data", hashMapOf())

    /**
     * Adds a data source property that's used for HikariCP initiation.
     */
    fun addDataSourceProperty(key: String, value: String) {
        dataSourceProperties[key] = value
    }

}

