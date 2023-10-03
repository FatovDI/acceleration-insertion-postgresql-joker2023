package com.example.postgresqlinsertion.batchinsertion.impl.repository

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.batchinsertion.impl.saver.CopyByEntitySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.sql.DataSource
import kotlin.reflect.KClass

class ConcurrentSaverHandler<E : BaseEntity>(
    private val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    private val dataSource: DataSource,
    private val batchSize: Int,
    private val countOfSaver: Int = 4,
) {
    private var counter = 0
    private val savers = (1..countOfSaver)
        .map { SaverJob(CopyByEntitySaver(processor, entityClass, dataSource.connection, batchSize)) }

    fun addDataForSave(entity: E) {
        val currSaver = savers[counter++ % countOfSaver]

        runBlocking {
            currSaver.job?.await()
            currSaver.job = async { currSaver.saver.addDataForSave(entity) }
        }

    }

    fun commit() {
        runBlocking {
            savers.forEach {
                it.job?.await()
                it.saver.commit()
                it.saver.close()
            }
        }
    }

    private data class SaverJob<E : BaseEntity>(
        val saver: BatchInsertionByEntitySaver<E>,
        var job: Deferred<Unit>? = null
    )
}
