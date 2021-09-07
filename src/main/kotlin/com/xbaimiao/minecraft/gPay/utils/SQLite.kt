package com.xbaimiao.minecraft.gPay.utils

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class SQLite(val file: File) {

    val connection: Connection

    val statement: Statement

    init {
        Class.forName("org.sqlite.JDBC")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        connection = DriverManager.getConnection("jdbc:sqlite:${file.path}")
        statement = connection.createStatement()
    }

    fun execute(sql: String) {
        statement.executeUpdate(sql)
    }

    fun query(sql: String): ResultSet {
        return statement.executeQuery(sql)
    }

    fun close(){
        statement.close()
        connection.close()
    }

}