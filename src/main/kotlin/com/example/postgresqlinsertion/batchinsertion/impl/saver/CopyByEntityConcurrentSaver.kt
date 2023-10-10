package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.exception.BatchInsertionException
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.sql.Connection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import kotlin.reflect.KClass

class CopyByEntityConcurrentSaver<E : BaseEntity>(
    processor: BatchInsertionByEntityProcessor,
    entityClass: KClass<E>,
    conn: Connection,
    batchSize: Int,
    private val executorService: ExecutorService
) : CopyByEntitySaver<E>(processor, entityClass, conn, batchSize) {

    private var saveDataJob: Future<*>? = null

    override fun addDataForSave(entity: E) {
        checkSaveDataJob()
        super.addDataForSave(entity)
    }

    override fun saveData() {
        checkSaveDataJob()
        saveDataJob = executorService.submit { super.saveData() }
    }

    override fun commit() {
        checkSaveDataJob()
        super.saveData()
        conn.commit()
    }

    private fun checkSaveDataJob() {
        try {
            saveDataJob?.get()
        } catch (e: Exception) {
            throw BatchInsertionException("Can not execute sent data to DB", e)
        }
    }
}