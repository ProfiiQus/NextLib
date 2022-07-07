package com.github.profiiqus.nextlib.storage

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

    constructor() : this(
        StorageType.SQLITE,
        "localhost",
        3306,
        "minecraft",
        "root",
        "",
        "data",
        hashMapOf()
    )

    constructor(storageType: StorageType) : this(
        storageType,
        "localhost",
        3306,
        "minecraft",
        "root",
        "",
        "data",
        hashMapOf()
    )

    constructor(
        storageType: StorageType,
        hostname: String,
        port: Int,
        database: String,
        username: String,
        password: String
    ) : this(storageType, hostname, port, database, username, password, "data", hashMapOf())

    fun addDataSourceProperty(key: String, value: String) {
        dataSourceProperties[key] = value
    }

}

