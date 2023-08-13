package com.example.postgresqlinsertion.logic.service

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.batchinsertion.impl.factory.BatchInsertionByPropertyFactory
import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
 class BatchInsertionPaymentDocumentByPropertyFactory(
    processor: BatchInsertionByPropertyProcessor,
    dataSource: DataSource,
 ) : BatchInsertionByPropertyFactory<PaymentDocumentEntity>(PaymentDocumentEntity::class, processor, dataSource)