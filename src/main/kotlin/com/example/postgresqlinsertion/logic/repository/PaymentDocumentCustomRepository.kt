package com.example.postgresqlinsertion.logic.repository

import com.example.postgresqlinsertion.batchinsertion.impl.repository.CopySaverBatchRepository
import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import org.springframework.stereotype.Component

@Component
class PaymentDocumentCustomRepository(
    val repo: PaymentDocumentRepository,
) : PaymentDocumentRepository by repo, CopySaverBatchRepository<PaymentDocumentEntity>(PaymentDocumentEntity::class)