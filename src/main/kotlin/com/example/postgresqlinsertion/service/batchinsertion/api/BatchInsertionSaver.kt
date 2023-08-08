package com.example.postgresqlinsertion.service.batchinsertion.api

/**
 * For save or update model
 */
interface BatchInsertionSaver<E>: AutoCloseable {

    /**
     * add entity for save
     * @param entity - entity
     */
    fun addDataForSave(entity: E)

    /**
     * send data to DB
     */
    fun saveData()

    /**
     * commit data to DB
     */
    fun commit()

}
