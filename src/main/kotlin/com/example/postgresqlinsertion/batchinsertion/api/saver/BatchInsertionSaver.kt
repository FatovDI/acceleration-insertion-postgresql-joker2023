package com.example.postgresqlinsertion.batchinsertion.api.saver

/**
 * For save or update data
 */
interface BatchInsertionSaver: AutoCloseable {

    /**
     * commit data to DB
     */
    fun commit()

}
