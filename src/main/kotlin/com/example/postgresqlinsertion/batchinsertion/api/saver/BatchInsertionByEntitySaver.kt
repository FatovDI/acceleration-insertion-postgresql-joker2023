package com.example.postgresqlinsertion.batchinsertion.api.saver

import com.example.postgresqlinsertion.logic.entity.BaseEntity

/**
 * For save or update entity
 */
interface BatchInsertionByEntitySaver<E: BaseEntity>: BatchInsertionSaver {

    /**
     * add entity for save
     * @param entity - entity
     */
    fun addDataForSave(entity: E)

    /**
     * send data to DB
     */
    fun saveData()

}
