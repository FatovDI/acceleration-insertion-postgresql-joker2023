package com.example.postgresqlinsertion.batchinsertion.api.processor

import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.io.BufferedWriter
import java.io.Reader
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * For save or update Map<out KProperty1<out BaseEntity, *>, String?>
 */
interface BatchInsertionByPropertyProcessor {

    /**
     * add data for create by map of property
     * @param data - map of property and value as string. Example: PaymentDocumentEntity::operationDt
     * @param writer - BufferedWriter for write entity to file
     * @param delimiter - delimiter for separate data
     * @param nullValue - string to define null value
     */
    fun addDataForCreate(
        data: Map<out KProperty1<out BaseEntity, *>, String?>,
        writer: BufferedWriter,
        delimiter: String,
        nullValue: String
    )

    /**
     * get string for update by map of property
     * @param data - map of property and value as string. Example: PaymentDocumentEntity::operationDt
     * @param id - id entity
     * @param nullValue - string to define null value
     * @return String - string for update
     */
    fun getStringForUpdate(data: Map<out KProperty1<out BaseEntity, *>, String?>, id: Long, nullValue: String): String

    /**
     * get string for update by map of property
     * @param data - map of property and value as string. Example: PaymentDocumentEntity::operationDt
     * @param nullValue - string to define null value
     * @return String - string for update
     */
    fun getStringForInsert(data: Map<out KProperty1<out BaseEntity, *>, String?>, nullValue: String): String

    /**
     * save data via file by property
     * @param clazz - entity class
     * @param columns - set of entity property
     * @param delimiter - delimiter for separate data
     * @param nullValue - string to define null value
     * @param from - data for save
     * @param conn - DB connection
     */
    fun saveToDataBaseByCopyMethod(
        clazz: KClass<out BaseEntity>,
        columns: Set<KProperty1<out BaseEntity, *>>,
        delimiter: String,
        nullValue: String,
        from: Reader,
        conn: Connection
    )

    /**
     * save list data with insert method by property
     * @param clazz - entity class
     * @param columns - set of entity property
     * @param data - list of string
     * @param conn - DB connection
     */
    fun insertDataToDataBase(
        clazz: KClass<out BaseEntity>, columns: Set<KProperty1<out BaseEntity, *>>, data: List<String>, conn: Connection
    )

    /**
     * save list data with update method by set of property
     * @param clazz - entity class
     * @param columns - set of entity property
     * @param data - list of string
     * @param conn - DB connection
     */
    fun updateDataToDataBase(
        clazz: KClass<out BaseEntity>, columns: Set<KProperty1<out BaseEntity, *>>, data: List<String>, conn: Connection
    )

}
