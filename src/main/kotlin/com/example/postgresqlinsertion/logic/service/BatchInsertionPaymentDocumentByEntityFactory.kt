package com.example.postgresqlinsertion.logic.service

import com.example.postgresqlinsertion.batchinsertion.impl.factory.BatchInsertionByEntityFactory
import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import org.springframework.stereotype.Component

@Component
class BatchInsertionPaymentDocumentByEntityFactory :
    BatchInsertionByEntityFactory<PaymentDocumentEntity>(PaymentDocumentEntity::class)