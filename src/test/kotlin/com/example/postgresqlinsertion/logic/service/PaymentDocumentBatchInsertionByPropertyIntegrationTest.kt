package com.example.postgresqlinsertion.logic.service

import com.example.postgresqlinsertion.batchinsertion.api.factory.BatchInsertionByPropertyFactory
import com.example.postgresqlinsertion.batchinsertion.api.factory.SaverType
import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import kotlin.reflect.KProperty1

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
internal class PaymentDocumentBatchInsertionByPropertyIntegrationTest {

    @Autowired
    lateinit var batchInsertionFactory: BatchInsertionByPropertyFactory<PaymentDocumentEntity>

    @Autowired
    lateinit var service: PaymentDocumentService

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `save data via copy method`() {
        val orderNumber = "p111"
        val orderDate = LocalDate.now()
        val paymentPurpose = "save data via copy method"

        batchInsertionFactory.getSaver(SaverType.COPY).use { saver ->
            val data = mutableMapOf(
                PaymentDocumentEntity::paymentPurpose to paymentPurpose,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(data)
            saver.saveData(data.keys)
            saver.commit()
        }

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isGreaterThan(0)
        assertThat(savedPd.first().paymentPurpose).isEqualTo(paymentPurpose)
    }

    @Test
    fun `save several data via copy method`() {
        val orderNumber = "p222"
        val orderDate = LocalDate.now()
        val paymentPurpose = "save several data via copy method"

        batchInsertionFactory.getSaver(SaverType.COPY).use { saver ->
            val data = mutableMapOf(
                PaymentDocumentEntity::paymentPurpose to paymentPurpose,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(data)
            saver.addDataForSave(data)
            saver.saveData(data.keys)
            saver.commit()
        }

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isEqualTo(2)
        assertThat(savedPd[0].paymentPurpose).isEqualTo(paymentPurpose)
        assertThat(savedPd[1].paymentPurpose).isEqualTo(paymentPurpose)
    }

    @Test
    fun `save data via insert method`() {
        val orderNumber = "p333"
        val orderDate = LocalDate.now()
        val paymentPurpose = "save data via insert method"

        batchInsertionFactory.getSaver(SaverType.INSERT).use { saver ->
            val data = mutableMapOf(
                PaymentDocumentEntity::paymentPurpose to paymentPurpose,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(data)
            saver.saveData(data.keys)
            saver.commit()
        }

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isGreaterThan(0)
        assertThat(savedPd.first().paymentPurpose).isEqualTo(paymentPurpose)
    }

    @Test
    fun `save several data via insert method`() {
        val orderNumber = "p444"
        val orderDate = LocalDate.now()
        val paymentPurpose = "save several data via insert method"

        batchInsertionFactory.getSaver(SaverType.INSERT).use { saver ->
            val data = mutableMapOf(
                PaymentDocumentEntity::paymentPurpose to paymentPurpose,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(data)
            saver.addDataForSave(data)
            saver.saveData(data.keys)
            saver.commit()
        }

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isEqualTo(2)
        assertThat(savedPd[0].paymentPurpose).isEqualTo(paymentPurpose)
        assertThat(savedPd[1].paymentPurpose).isEqualTo(paymentPurpose)
    }


    @Test
    fun `update data via insert method`() {
        val orderNumber = "p555"
        val orderDate = LocalDate.now()
        val paymentPurposeIns = "save data via insert method"
        val paymentPurposeUpd = "update data via insert method"
        batchInsertionFactory.getSaver(SaverType.INSERT).use { saver ->
            val insertData = mutableMapOf(
                PaymentDocumentEntity::paymentPurpose to paymentPurposeIns,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(insertData)
            saver.saveData(insertData.keys)
            saver.commit()
        }
        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isEqualTo(1)
        val updateData = mutableMapOf(
            PaymentDocumentEntity::id to savedPd.first().id,
            PaymentDocumentEntity::paymentPurpose to paymentPurposeUpd,
            PaymentDocumentEntity::orderNumber to orderNumber,
            PaymentDocumentEntity::orderDate to orderDate,
            PaymentDocumentEntity::prop15 to "END"
        )

        batchInsertionFactory.getSaver(SaverType.UPDATE).use { saver ->
            saver.addDataForSave(updateData)
            saver.saveData(updateData.keys)
            saver.commit()
        }

        val updatedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(updatedPd.size).isGreaterThan(0)
        assertThat(updatedPd.first().paymentPurpose).isEqualTo(paymentPurposeUpd)
    }

    @Test
    fun `update several data via insert method`() {
        val orderNumber = "p666"
        val orderDate = LocalDate.now()
        val paymentPurposeIns = "save several data via insert method"
        val paymentPurposeUpd = "update several data via insert method"
        batchInsertionFactory.getSaver(SaverType.INSERT).use { saver ->
            val insertData = mutableMapOf(
                PaymentDocumentEntity::paymentPurpose to paymentPurposeIns,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(insertData)
            saver.addDataForSave(insertData)
            saver.saveData(insertData.keys)
            saver.commit()
        }
        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isEqualTo(2)
        val updateData = mutableMapOf(
            PaymentDocumentEntity::id to savedPd.first().id,
            PaymentDocumentEntity::paymentPurpose to paymentPurposeUpd,
            PaymentDocumentEntity::orderNumber to orderNumber,
            PaymentDocumentEntity::orderDate to orderDate,
            PaymentDocumentEntity::prop15 to "END"
        )

        batchInsertionFactory.getSaver(SaverType.UPDATE).use { saver ->
            saver.addDataForSave(updateData)
            saver.addDataForSave(updateData.apply { put(PaymentDocumentEntity::id, savedPd[1].id) })
            saver.saveData(updateData.keys)
            saver.commit()
        }

        val updatedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(updatedPd.size).isEqualTo(2)
        assertThat(updatedPd[0].paymentPurpose).isEqualTo(paymentPurposeUpd)
        assertThat(updatedPd[1].paymentPurpose).isEqualTo(paymentPurposeUpd)
    }

    @Test
    fun `save data via copy method by binary`() {
        val orderNumber = "p777_b"
        val orderDate = LocalDate.now()
        val paymentPurpose = "save data via copy method by binary"

        batchInsertionFactory.getSaver(SaverType.COPY_BINARY).use { saver->

            val data = mutableMapOf<KProperty1<PaymentDocumentEntity, *>, Any?>(
                PaymentDocumentEntity::paymentPurpose to paymentPurpose,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(data)
            saver.saveData(data.keys)
            saver.commit()

        }

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isGreaterThan(0)
        assertThat(savedPd.first().paymentPurpose).isEqualTo(paymentPurpose)
    }

    @Test
    fun `save several data via copy method by binary`() {
        val orderNumber = "p888_b"
        val orderDate = LocalDate.now()
        val paymentPurpose = "save several data via copy method by binary"

        batchInsertionFactory.getSaver(SaverType.COPY_BINARY).use { saver ->
            val data = mutableMapOf(
                PaymentDocumentEntity::paymentPurpose to paymentPurpose,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(data)
            saver.addDataForSave(data)
            saver.saveData(data.keys)
            saver.commit()
        }

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isEqualTo(2)
        assertThat(savedPd[0].paymentPurpose).isEqualTo(paymentPurpose)
        assertThat(savedPd[1].paymentPurpose).isEqualTo(paymentPurpose)
    }


    @Test
    fun `save data via copy method by binary file`() {
        val orderNumber = "p777_bf"
        val orderDate = LocalDate.now()
        val paymentPurpose = "save data via copy method by binary file"

        batchInsertionFactory.getSaver(SaverType.COPY_BINARY_VIA_FILE).use { saver->

            val data = mutableMapOf<KProperty1<PaymentDocumentEntity, *>, Any?>(
                PaymentDocumentEntity::paymentPurpose to paymentPurpose,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(data)
            saver.saveData(data.keys)
            saver.commit()

        }

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isGreaterThan(0)
        assertThat(savedPd.first().paymentPurpose).isEqualTo(paymentPurpose)
    }

    @Test
    fun `save several data via copy method by binary file`() {
        val orderNumber = "p888_bf"
        val orderDate = LocalDate.now()
        val paymentPurpose = "save several data via copy method by binary file"

        batchInsertionFactory.getSaver(SaverType.COPY_BINARY).use { saver ->
            val data = mutableMapOf(
                PaymentDocumentEntity::paymentPurpose to paymentPurpose,
                PaymentDocumentEntity::orderNumber to orderNumber,
                PaymentDocumentEntity::orderDate to orderDate,
                PaymentDocumentEntity::prop15 to "END"
            )

            saver.addDataForSave(data)
            saver.addDataForSave(data)
            saver.saveData(data.keys)
            saver.commit()
        }

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        assertThat(savedPd.size).isEqualTo(2)
        assertThat(savedPd[0].paymentPurpose).isEqualTo(paymentPurpose)
        assertThat(savedPd[1].paymentPurpose).isEqualTo(paymentPurpose)
    }

}
