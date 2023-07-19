package com.example.postgresqlinsertion.service.batchinsertion.api

import com.example.postgresqlinsertion.entity.BaseEntity
import java.io.BufferedWriter
import java.io.Reader
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * For save or update Map<out KProperty1<out BaseEntity, *>, String?>
 */
interface IBatchInsertionByPropertyProcessor {

    /**
     * add data for create by map of property
     * @param data - map of property and value as string. Example: PaymentDocumentEntity::operationDt
     * @param writer - BufferedWriter for write entity to file
     */
    fun addDataForCreate(data: Map<out KProperty1<out BaseEntity, *>, String?>, writer: BufferedWriter)

    /**
     * get string for update by map of property
     * @param data - map of property and value as string. Example: PaymentDocumentEntity::operationDt
     * @param id - id entity
     * @return String - string for update
     */
    fun getStringForUpdate(data: Map<out KProperty1<out BaseEntity, *>, String?>, id: Long): String

    /**
     * get string for update by map of property
     * @param data - map of property and value as string. Example: PaymentDocumentEntity::operationDt
     * @return String - string for update
     */
    fun getStringForInsert(data: Map<out KProperty1<out BaseEntity, *>, String?>): String

    /**
     * save data via file by property
     * @param clazz - entity class
     * @param columns - set of entity property
     * @param from - data for save
     */
    fun saveToDataBaseByCopyMethod(clazz: KClass<out BaseEntity>, columns: Set<out KProperty1<out BaseEntity, *>>, from: Reader)

    /**
     * save list data with insert method by property
     * @param clazz - entity class
     * @param columns - set of entity property
     * @param data - list of string
     */
    fun insertDataToDataBase(clazz: KClass<out BaseEntity>, columns: Set<out KProperty1<out BaseEntity, *>>, data: List<String>)

    /**
     * save list data with update method by set of property
     * @param clazz - entity class
     * @param columns - set of entity property
     * @param data - list of string
     */
    fun updateDataToDataBase(clazz: KClass<out BaseEntity>, columns: Set<out KProperty1<out BaseEntity, *>>, data: List<String>)

}
