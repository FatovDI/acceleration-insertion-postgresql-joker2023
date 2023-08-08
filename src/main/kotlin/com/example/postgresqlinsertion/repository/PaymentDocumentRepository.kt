package com.example.postgresqlinsertion.repository

import com.example.postgresqlinsertion.entity.PaymentDocumentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentDocumentRepository: JpaRepository<PaymentDocumentEntity, Long>