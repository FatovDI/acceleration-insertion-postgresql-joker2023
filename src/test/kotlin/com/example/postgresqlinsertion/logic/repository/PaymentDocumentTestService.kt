package com.example.postgresqlinsertion.logic.repository

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.impl.saver.CopyByEntitySaver
import com.example.postgresqlinsertion.logic.entity.AccountEntity
import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.jdbc.datasource.ConnectionHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDate
import javax.sql.DataSource

@Service
class PaymentDocumentTestService(
    private val pdCustomRepository: PaymentDocumentCustomRepository,
    @Lazy
    private val selfService: PaymentDocumentTestService
) {

    @Autowired
    lateinit var transactionManager: PlatformTransactionManager

    @Autowired
    lateinit var processor: BatchInsertionByEntityProcessor

    @Autowired
    lateinit var dataSource: DataSource

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

    @Transactional
    fun saveSeveralDataWithCopySaver(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )

        val conn = (TransactionSynchronizationManager.getResource(dataSource) as ConnectionHolder).connection

        val saver = CopyByEntitySaver(processor, PaymentDocumentEntity::class, conn, 10)

        for (i in 0 until count) {
            saver.addDataForSave(
                PaymentDocumentEntity(
                    paymentPurpose = paymentPurpose,
                    orderNumber = orderNumber,
                    orderDate = orderDate,
                    prop15 = "END"
                )
            )
        }

        saver.saveData()

    }

    @Transactional
    fun saveSeveralDataInTwoTransactionWithRequiredNew(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )

        selfService.saveSeveralDataInRequiredNewTransaction(count, orderNumber, orderDate)

        selfService.saveSeveralDataInRequiredNewTransaction(count, orderNumber, orderDate)


    }

    @Transactional
    fun saveSeveralDataInTwoTransactionWithRequiredNewAndException(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )

        selfService.saveSeveralDataInRequiredNewTransaction(count, orderNumber, orderDate)

        throw RuntimeException("Unexpected error for rollback")

    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveSeveralDataInRequiredNewTransaction(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        for (i in 0 until count) {
            pdCustomRepository.save(
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
    fun saveSeveralDataInTwoTransactionWithRequiredNewAndIncorrectData(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                account = AccountEntity().apply { id = 1 },
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )

        selfService.saveSeveralDataInRequiredNewTransaction(count, orderNumber, orderDate)

    }

    @Transactional
    fun saveSeveralDataInManualTransaction(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )

        val transactionTemplate = TransactionTemplate(transactionManager)
        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        transactionTemplate.executeWithoutResult { status ->
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
    }

    @Transactional
    fun saveSeveralDataWithSaveAllByCopy(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )

        (1..count).map {
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        }.let { pdCustomRepository.saveAllByCopy(it) }

    }

    @Transactional
    fun saveSeveralDataWithSaveAllByCopyInOneTransaction(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )

        (1..count).map {
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        }.let { pdCustomRepository.saveAllByCopy(it) }

        (1..count).map {
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        }.let { pdCustomRepository.saveAllByCopy(it) }

    }

    @Transactional
    fun saveSeveralDataWithSaveAllByCopyInNewTransaction(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        pdCustomRepository.save(
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        )


        (1..count).map {
            PaymentDocumentEntity(
                paymentPurpose = paymentPurpose,
                orderNumber = orderNumber,
                orderDate = orderDate,
                prop15 = "END"
            )
        }.let { pdCustomRepository.saveAllByCopy(it) }

        val transactionTemplate = TransactionTemplate(transactionManager)
        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        transactionTemplate.executeWithoutResult { status ->
            (1..count).map {
                PaymentDocumentEntity(
                    paymentPurpose = paymentPurpose,
                    orderNumber = orderNumber,
                    orderDate = orderDate,
                    prop15 = "END"
                )
            }.let { pdCustomRepository.saveAllByCopy(it) }
        }

    }

    @Transactional
    fun saveDataWithConcurrentByCopy(count: Int, orderNumber: String, orderDate: LocalDate) {

        val paymentPurpose = "save entity via copy method"

        for (i in 0 until count) {
            pdCustomRepository.saveByCopyConcurrent(
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
    fun saveSeveralDataWithConcurrentByCopy(count: Int, orderNumber: String, orderDate: LocalDate) {

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
            pdCustomRepository.saveByCopyConcurrent(
                PaymentDocumentEntity(
                    paymentPurpose = paymentPurpose,
                    orderNumber = orderNumber,
                    orderDate = orderDate,
                    prop15 = "END"
                )
            )
        }

    }

    fun findAllByOrderNumberAndOrderDate(orderNumber: String, orderDate: LocalDate): List<PaymentDocumentEntity> {
        return pdCustomRepository.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
    }

}
