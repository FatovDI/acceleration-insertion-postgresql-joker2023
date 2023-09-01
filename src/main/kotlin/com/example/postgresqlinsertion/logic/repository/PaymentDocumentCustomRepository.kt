package com.example.postgresqlinsertion.logic.repository

import com.example.postgresqlinsertion.batchinsertion.api.factory.BatchInsertionByEntityFactory
import com.example.postgresqlinsertion.batchinsertion.impl.saver.CopySaverRepository
import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import org.springframework.stereotype.Repository

@Repository
class PaymentDocumentCustomRepository(
    val repo: PaymentDocumentRepository,
    override val batchInsertionFactory: BatchInsertionByEntityFactory<PaymentDocumentEntity>,
) : PaymentDocumentRepository by repo, CopySaverRepository<PaymentDocumentEntity>()