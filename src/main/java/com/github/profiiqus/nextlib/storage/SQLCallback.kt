package com.github.profiiqus.nextlib.storage

import java.sql.ResultSet

/**
 * SQLCallback interface for asynchronous communication with the database.
 * @author ProfiiQus
 */
interface SQLCallback {

    /**
     * Method executed when the asynchronous query is completed.
     * The result of the SQL query is stored in the entry ResultSet.
     */
    fun onQueryDone(result: ResultSet)

}