package com.example.postgresqlinsertion.service

import com.example.postgresqlinsertion.entity.PaymentDocumentEntity
import com.example.postgresqlinsertion.service.batchinsertion.api.IBatchInsertionByEntityProcessor
import org.springframework.stereotype.Component
import com.example.postgresqlinsertion.service.batchinsertion.impl.BatchInsertionFactory
import javax.sql.DataSource

@Component
 class BatchInsertionPaymentDocumentFactory(
    processor: IBatchInsertionByEntityProcessor,
    dataSource: DataSource,
 ) : BatchInsertionFactory<PaymentDocumentEntity>(PaymentDocumentEntity::class, processor, dataSource)