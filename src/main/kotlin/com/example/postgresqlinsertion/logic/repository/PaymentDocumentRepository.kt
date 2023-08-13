package com.example.postgresqlinsertion.logic.repository

import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface PaymentDocumentRepository: JpaRepository<PaymentDocumentEntity, Long> {
    fun findAllByOrderNumberAndOrderDate(orderNumber: String, orderDate: LocalDate): List<PaymentDocumentEntity>
}