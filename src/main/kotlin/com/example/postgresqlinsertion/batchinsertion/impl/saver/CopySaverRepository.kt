package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.factory.BatchInsertionByEntityFactory
import com.example.postgresqlinsertion.batchinsertion.api.factory.SaverType
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.batchinsertion.exception.BatchInsertionException
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

abstract class CopySaverRepository<E : BaseEntity> {

    abstract val batchInsertionFactory: BatchInsertionByEntityFactory<E>

    fun saveByCopy(entity: E) {
        getCopySaver().addDataForSave(entity)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getCopySaver(): BatchInsertionByEntitySaver<E> {

        val copySaverResourceName = "BatchInsertionCopySaver"

        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw BatchInsertionException("Transaction is not active. Batch insertion by saver is not available.")
        }

        return TransactionSynchronizationManager.getResource(copySaverResourceName)
            ?.let { it as BatchInsertionByEntitySaver<E> }
            ?: let {

                val saver = batchInsertionFactory.getSaver(SaverType.COPY_VIA_FILE)

                TransactionSynchronizationManager.registerSynchronization(
                    object : TransactionSynchronization {
                        override fun afterCompletion(status: Int) {
                            if (status == 0) {
                                saver.saveData()
                                saver.commit()
                            }
                            saver.close()
                            TransactionSynchronizationManager.unbindResource("BatchInsertionCopySaver")
                        }
                    }
                )

                TransactionSynchronizationManager.bindResource(copySaverResourceName, saver)
                saver
            }

    }

}