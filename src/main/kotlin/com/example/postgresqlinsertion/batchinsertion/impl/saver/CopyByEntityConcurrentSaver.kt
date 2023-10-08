package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.exception.BatchInsertionException
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.sql.Connection
import kotlin.concurrent.thread
import kotlin.reflect.KClass

class CopyByEntityConcurrentSaver<E : BaseEntity>(
    processor: BatchInsertionByEntityProcessor,
    entityClass: KClass<E>,
    conn: Connection,
    batchSize: Int
) : CopyByEntitySaver<E>(processor, entityClass, conn, batchSize) {

    private var saveDataJob: Thread? = null

    override fun addDataForSave(entity: E) {
        checkSaveDataJob()
        super.addDataForSave(entity)
    }

    override fun saveData() {

        checkSaveDataJob()

        saveDataJob = thread(isDaemon = true) {
            try {
                super.saveData()
            } catch (e: Exception) {
                Thread.currentThread().interrupt()
            }
        }
    }

    override fun commit() {
        checkSaveDataJob()
        super.saveData()
        conn.commit()
    }

    private fun checkSaveDataJob() {
        saveDataJob?.join()
        saveDataJob?.takeIf { it.isInterrupted }?.let { throw BatchInsertionException("Can not execute sent data to DB") }
    }
}