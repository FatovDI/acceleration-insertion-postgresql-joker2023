@file:OptIn(DelicateCoroutinesApi::class)

package com.example.postgresqlinsertion.batchinsertion.impl.repository

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionSaver
import com.example.postgresqlinsertion.batchinsertion.exception.BatchInsertionException
import com.example.postgresqlinsertion.batchinsertion.impl.saver.CopyByEntitySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.datasource.ConnectionHolder
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.concurrent.Executors
import javax.sql.DataSource
import kotlin.math.ceil
import kotlin.reflect.KClass

abstract class CopySaverBatchRepository<E : BaseEntity>(
    val entityClass: KClass<E>
) {

    @Value("\${batch_insertion.batch_size}")
    private var batchSize: Int = 100

    @Value("\${batch_insertion.pool_size}")
    private var poolSize: Int = 4

    @Value("\${batch_insertion.concurrent_saves}")
    private var concurrentSavers: Int = 1

    @Autowired
    private lateinit var processor: BatchInsertionByEntityProcessor

    @Autowired
    private lateinit var dataSource: DataSource

    private val executorService by lazy {
        Executors.newFixedThreadPool(poolSize)
    }

    private val concurrentSaverHandlerName = "ConcurrentSaverHandler"
    private val copySaverResourceName = "BatchInsertionCopySaver"

    fun saveByCopy(entity: E) {
        getCopySaver().addDataForSave(entity)
    }

    @Suppress("UNCHECKED_CAST")
    fun saveByCopyConcurrent(entity: E) {

        checkTransactionIsOpen()

        val handler = TransactionSynchronizationManager.getResource(concurrentSaverHandlerName)
            ?.let { it as ConcurrentSaverHandler<E> }
            ?: let {
                val handler = ConcurrentSaverHandler(
                    processor = processor,
                    entityClass = entityClass,
                    dataSource = dataSource,
                    batchSize = batchSize,
                    numberOfSavers = concurrentSavers,
                    executorService = executorService
                )

                TransactionSynchronizationManager.registerSynchronization(
                    object : TransactionSynchronization {
                        override fun beforeCommit(readOnly: Boolean) {
                            super.beforeCommit(readOnly)
                            handler.commit()
                        }
                        override fun afterCompletion(status: Int) {
                            if (status != 0) {
                                handler.rollback()
                            }
                            TransactionSynchronizationManager.unbindResource(concurrentSaverHandlerName)
                        }
                    }
                )

                TransactionSynchronizationManager.bindResource(concurrentSaverHandlerName, handler)
                handler
            }

        handler.addDataForSave(entity)

    }

    @Suppress("UNCHECKED_CAST")
    fun saveAllByCopy(entities: List<E>) {

        val jobsSaveAllWithCoroutineName = "JobsSaveAllWithCoroutine"

        checkTransactionIsOpen()

        val jobs = runBlocking {
            entities.chunked(ceil(entities.size.toDouble()/concurrentSavers).toInt()).map {
                async(Dispatchers.Default) { saveBatchBySaveAll(it) }
            }
        }.toMutableList()

        TransactionSynchronizationManager.getResource(jobsSaveAllWithCoroutineName)
            ?.let { it as  MutableList<Deferred<BatchInsertionSaver>> }
            ?.let { it.apply { addAll(jobs) } }
            ?: let {
                TransactionSynchronizationManager.bindResource(jobsSaveAllWithCoroutineName, jobs)

                TransactionSynchronizationManager.registerSynchronization(
                    object : TransactionSynchronization {
                        override fun beforeCommit(readOnly: Boolean) {
                            super.beforeCommit(readOnly)

                            runBlocking {
                                TransactionSynchronizationManager.getResource(jobsSaveAllWithCoroutineName)
                                    ?.let { it as  MutableList<Deferred<BatchInsertionSaver>> }
                                    ?.map { it.await() }
                                    ?.forEach {
                                        it.commit()
                                        it.close()
                                    }
                            }
                        }
                        override fun afterCompletion(status: Int) {
                            TransactionSynchronizationManager.unbindResource(jobsSaveAllWithCoroutineName)
                        }
                    }
                )
            }

    }

    private fun saveBatchBySaveAll(entities: List<E>): BatchInsertionSaver {
        val saver = CopyByEntitySaver(processor, entityClass, dataSource.connection, batchSize)

        entities.forEach { saver.addDataForSave(it) }

        return saver
    }

    @Suppress("UNCHECKED_CAST")
    private fun getCopySaver(): BatchInsertionByEntitySaver<E> {

        checkTransactionIsOpen()

        return TransactionSynchronizationManager.getResource(copySaverResourceName)
            ?.let { it as BatchInsertionByEntitySaver<E> }
            ?: let {

                val conn = (TransactionSynchronizationManager.getResource(dataSource) as ConnectionHolder).connection

                val saver = CopyByEntitySaver(processor, entityClass, conn, batchSize)

                TransactionSynchronizationManager.registerSynchronization(
                    object : TransactionSynchronization {
                        override fun beforeCommit(readOnly: Boolean) {
                            super.beforeCommit(readOnly)
                            saver.saveData()
                        }
                        override fun afterCompletion(status: Int) {
                            TransactionSynchronizationManager.unbindResource(copySaverResourceName)
                        }
                    }
                )

                TransactionSynchronizationManager.bindResource(copySaverResourceName, saver)
                saver
            }

    }

    private fun checkTransactionIsOpen() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw BatchInsertionException("Transaction is not active. Batch insertion by saver is not available.")
        }
    }

}
