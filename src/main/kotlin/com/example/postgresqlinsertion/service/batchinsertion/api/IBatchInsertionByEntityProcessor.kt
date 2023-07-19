package com.example.postgresqlinsertion.service.batchinsertion.api

import com.example.postgresqlinsertion.entity.BaseEntity
import java.io.BufferedWriter
import java.io.Reader
import java.sql.Connection
import kotlin.reflect.KClass

/**
 * For save or update entity or Map<out KProperty1<out BaseEntity, *>, String?>
 */
interface IBatchInsertionByEntityProcessor{
    /**
     * add data for create by entity via file
     * @param data - entity
     * @param writer - BufferedWriter for write entity to file
     */
    fun addDataForCreate(data: BaseEntity, writer: BufferedWriter)

    /**
     * get string for update by entity
     * @param data - entity
     * @return String - string for update
     */
    fun getStringForUpdate(data: BaseEntity): String

    /**
     * get string for insert by entity
     * @param data - entity
     * @return String - string for insert
     */
    fun getStringForInsert(data: BaseEntity): String

    /**
     * save data via copy method
     * @param clazz - entity class
     * @param from - data for save
     * @param conn - DB connection
     */
    fun saveToDataBaseByCopyMethod(
        clazz: KClass<out BaseEntity>,
        from: Reader,
        conn: Connection
    )

    /**
     * save list data with insert method
     * @param clazz - entity class
     * @param data - list of string
     * @param conn - DB connection
     */
    fun insertDataToDataBase(clazz: KClass<out BaseEntity>, data: List<String>, conn: Connection)

    /**
     * save list data with update method
     * @param clazz - entity class
     * @param data - list of string
     * @param conn - DB connection
     */
    fun updateDataToDataBase(clazz: KClass<out BaseEntity>, data: List<String>, conn: Connection)

}
