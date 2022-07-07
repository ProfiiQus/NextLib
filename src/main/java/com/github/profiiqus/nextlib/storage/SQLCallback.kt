package com.github.profiiqus.nextlib.storage

import java.sql.ResultSet

interface SQLCallback {

    fun onQueryDone(result: ResultSet)

}