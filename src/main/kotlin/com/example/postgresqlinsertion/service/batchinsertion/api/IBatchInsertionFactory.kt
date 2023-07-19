package com.example.postgresqlinsertion.service.batchinsertion.api

import com.example.postgresqlinsertion.entity.BaseEntity

/**
 * Interface should be realised for save model via batch insertion.
 *
 * example:
 *      @Component
 *       class BatchInsertionPaymentDocumentFactory(
 *       processor: IBatchInsertionProcessor,
 *       ) : BatchInsertionFactory<PaymentDocumentModel, PaymentDocumentEntity>(PaymentDocumentEntity::class, processor)
 *
 */
interface IBatchInsertionFactory<E: BaseEntity> {

    val processor: IBatchInsertionByEntityProcessor

    /**
     * get saver by enum
     * @param type - saver type. Can be COPY, INSERT, UPDATE
     */
    fun getSaver(type: SaverType): IBatchInsertionSaver<E>

}

enum class SaverType {
    COPY,
    COPY_VIA_FILE,
    INSERT,
    UPDATE
}
