package com.example.postgresqlinsertion.logic.repository

import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.transaction.Transactional

@Service
class PaymentDocumentTestService(
    private val pdCustomRepository: PaymentDocumentCustomRepository
) {

    @Transactional
    fun saveByCopyViaSpring(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        for (i in 0 until count) {
            pdCustomRepository.saveByCopy(
                PaymentDocumentEntity(
                    paymentPurpose = paymentPurpose,
                    orderNumber = orderNumber,
                    orderDate = orderDate,
                    prop15 = "END"
                )
            )
        }

    }

    @Transactional
    fun saveSeveralDataInOneTransaction(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )

        for (i in 0 until count) {
            pdCustomRepository.saveByCopy(
                PaymentDocumentEntity(
                    paymentPurpose = paymentPurpose,
                    orderNumber = orderNumber,
                    orderDate = orderDate,
                    prop15 = "END"
                )
            )
        }

    }

    @Transactional
    fun saveSeveralDataInOneTransactionWithRollback(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )

        for (i in 0 until count) {
            pdCustomRepository.saveByCopy(
                PaymentDocumentEntity(
                    paymentPurpose = paymentPurpose,
                    orderNumber = orderNumber,
                    orderDate = orderDate,
                    prop15 = "END"
                )
            )
        }

        throw RuntimeException("Unexpected error for rollback")

    }

    fun findAllByOrderNumberAndOrderDate(orderNumber: String, orderDate: LocalDate): List<PaymentDocumentEntity> {
        return pdCustomRepository.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
    }

}
