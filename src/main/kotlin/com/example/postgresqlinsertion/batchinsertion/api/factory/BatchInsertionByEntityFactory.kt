package com.example.postgresqlinsertion.batchinsertion.api.factory

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity

/**
 * Interface should be realised for save entity via batch insertion.
 *
 * example:
 *      @Component
 *       class BatchInsertionPaymentDocumentByEntityFactory(
 *       processor: BatchInsertionByEntityProcessor,
 *       ) : BatchInsertionByEntityFactory<PaymentDocumentEntity>(PaymentDocumentEntity::class, processor)
 *
 */
interface BatchInsertionByEntityFactory<E: BaseEntity> {

    val processor: BatchInsertionByEntityProcessor

    /**
     * get saver by enum
     * @param type - saver type. Can be COPY, INSERT, UPDATE
     */
    fun getSaver(type: SaverType): BatchInsertionByEntitySaver<E>

}