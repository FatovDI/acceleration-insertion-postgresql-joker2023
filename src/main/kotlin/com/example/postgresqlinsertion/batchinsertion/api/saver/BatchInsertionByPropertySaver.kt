package com.example.postgresqlinsertion.batchinsertion.api.saver

import com.example.postgresqlinsertion.logic.entity.BaseEntity
import kotlin.reflect.KProperty1

/**
 * For save or update data by property
 */
interface BatchInsertionByPropertySaver<E: BaseEntity>: BatchInsertionSaver {

    /**
     * add data for save
     * @param data - map of data where key it is property and value it is value
     */
    fun addDataForSave(data: Map<out KProperty1<E, *>, String?>)

    /**
     * send data to DB
     */
    fun saveData(columns: Set<KProperty1<E, *>>,)
}
