package com.example.postgresqlinsertion.logic.service

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.impl.factory.BatchInsertionByEntityFactory
import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
 class BatchInsertionPaymentDocumentByEntityFactory(
    processor: BatchInsertionByEntityProcessor,
    dataSource: DataSource,
 ) : BatchInsertionByEntityFactory<PaymentDocumentEntity>(PaymentDocumentEntity::class, processor, dataSource)