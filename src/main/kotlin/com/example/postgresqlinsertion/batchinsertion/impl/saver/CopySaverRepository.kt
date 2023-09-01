package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.factory.BatchInsertionByEntityFactory
import com.example.postgresqlinsertion.batchinsertion.api.factory.SaverType
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.batchinsertion.exception.BatchInsertionException
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class CopySaverRepository<E : BaseEntity>() {

    abstract val batchInsertionFactory: BatchInsertionByEntityFactory<E>

    lateinit var batchInsertionByTransaction: ConcurrentHashMap<String, BatchInsertionByEntitySaver<E>>

    fun saveByCopy(entity: E) {
        getCopySaver().addDataForSave(entity)
    }

    private fun getCopySaver(): BatchInsertionByEntitySaver<E> {

        if (!::batchInsertionByTransaction.isInitialized) {
            synchronized(this) {
                if (!::batchInsertionByTransaction.isInitialized) {
                    batchInsertionByTransaction = ConcurrentHashMap<String, BatchInsertionByEntitySaver<E>>()
                }
            }
        }

        val transactionName = if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.getCurrentTransactionName()
        } else {
            null
        } ?: throw BatchInsertionException("Transaction is not active")

        return batchInsertionByTransaction[transactionName] ?: let {
            val transactionNameUpd = UUID.randomUUID().toString()
            TransactionSynchronizationManager.setCurrentTransactionName(transactionNameUpd)

            val saver = batchInsertionFactory.getSaver(SaverType.COPY_VIA_FILE)

            TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronization {
                    override fun afterCommit() {
                        saver.saveData()
                        saver.commit()
                        batchInsertionByTransaction.remove(transactionNameUpd)
                    }
                }
            )

            batchInsertionByTransaction.getOrPut(transactionNameUpd) { saver }
        }

    }

}