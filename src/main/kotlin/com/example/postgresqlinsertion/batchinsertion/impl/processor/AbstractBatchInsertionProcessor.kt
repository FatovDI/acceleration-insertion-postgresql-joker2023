package com.example.postgresqlinsertion.batchinsertion.impl.processor

import com.example.postgresqlinsertion.batchinsertion.getColumnsStringByClass
import com.example.postgresqlinsertion.batchinsertion.getTableName
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
import java.io.Reader
import java.sql.Connection
import kotlin.reflect.KClass

/**
 * Abstract class for save and update data to DB
 */
abstract class AbstractBatchInsertionProcessor {

    /**
     * save data via copy method
     * @param clazz - entity class
     * @param delimiter - delimiter for separate data
     * @param nullValue - string to define null value
     * @param from - data for save
     * @param conn - DB connection
     */
    fun saveToDataBaseByCopyMethod(
        clazz: KClass<out BaseEntity>,
        from: Reader,
        delimiter: String,
        nullValue: String,
        conn: Connection
    ) {
        saveToDataBaseByCopyMethod(
            getTableName(clazz),
            getColumnsStringByClass(clazz),
            delimiter,
            nullValue,
            from,
            conn
        )
    }

    /**
     * save list data with insert method
     * @param clazz - entity class
     * @param data - list of string
     * @param conn - DB connection
     */
    fun insertDataToDataBase(clazz: KClass<out BaseEntity>, data: List<String>, conn: Connection) {
        insertDataToDataBase(getTableName(clazz), getColumnsStringByClass(clazz), data, conn)
    }

    /**
     * save list data with update method
     * @param clazz - entity class
     * @param data - list of string
     * @param conn - DB connection
     */
    fun updateDataToDataBase(clazz: KClass<out BaseEntity>, data: List<String>, conn: Connection) {
        updateDataToDataBase(getTableName(clazz), getColumnsStringByClass(clazz), data, conn)
    }

    /**
     * save list data with insert method
     * @param data - list of string
     * @param delimiter - delimiter for separate data
     * @param nullValue - string to define null value
     * @return String - string for insert
     */
    fun getStringForWrite(data: Collection<String?>, delimiter: String, nullValue: String): String {

        return data.joinToString(delimiter) {
            it
                ?.replace("\\", "\\\\")
                ?.replace("\n", "\\n")
                ?.replace("\r", "\\r")
                ?.replace(delimiter, "\\$delimiter")
                ?: nullValue
        }
    }

    /**
     * get string for insert by entity
     * @param data - entity
     * @param nullValue - string to define null value
     * @return String - string for insert
     */
    fun getStringForInsert(data: Collection<String?>, nullValue: String): String {
        return data
            .joinToString(",") { js ->
                js
                    ?.replace("'", "''")
                    ?.let { "'$it'" }
                    ?: nullValue
            }
    }

    /**
     * save data via copy method
     * @param tableName - table name in DB
     * @param columns - string of column separated by delimiter
     * @param delimiter - delimiter for separate data
     * @param nullValue - string to define null value
     * @param from - data for save
     * @param conn - DB connection
     */
    fun saveToDataBaseByCopyMethod(
        tableName: String,
        columns: String,
        delimiter: String,
        nullValue: String,
        from: Reader,
        conn: Connection
    ) {

        if (conn.isWrapperFor(BaseConnection::class.java)) {
            val copyManager = CopyManager(conn.unwrap(BaseConnection::class.java))
            copyManager.copyIn(
                "COPY $tableName ($columns) FROM STDIN (DELIMITER '$delimiter', NULL '$nullValue')",
                from
            )
        }
    }

    /**
     * save list data with insert method
     * @param tableName - table name in DB
     * @param columns - string of column separated by comma delimiter
     * @param data - list of string
     * @param conn - DB connection
     */
    fun insertDataToDataBase(tableName: String, columns: String, data: List<String>, conn: Connection) {

        conn.createStatement().use { stmt ->
            stmt.executeLargeUpdate(
                "INSERT INTO $tableName ($columns) VALUES ${
                    data
                        .mapIndexed { index, s -> "($s)${if (index == data.lastIndex) ";" else ","}" }
                        .joinToString("\n")
                }"
            )
        }
    }

    fun updateDataToDataBase(tableName: String, columns: String, data: List<String>, conn: Connection) {

        conn.createStatement().use { stmnt ->
            stmnt.executeLargeUpdate(
                data.joinToString("\n") { s ->
                    "UPDATE $tableName SET ($columns) = $s;"
                }
            )
        }
    }
}
