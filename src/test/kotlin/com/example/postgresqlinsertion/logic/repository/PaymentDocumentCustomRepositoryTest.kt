package com.example.postgresqlinsertion.logic.repository

import com.zaxxer.hikari.HikariDataSource
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import javax.sql.DataSource

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
internal class PaymentDocumentCustomRepositoryTest {

    @Autowired
    lateinit var service: PaymentDocumentTestService

    @Autowired
    lateinit var dataSource: DataSource

    @Test
    fun `save payment document by custom repository test`() {
        val orderNumber = "CR_4"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveByCopyViaSpring(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count)
    }

    @Test
    fun `save several payment document by custom repository in one transaction test`() {
        val orderNumber = "CR_5"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveSeveralDataInOneTransaction(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count+1)
    }

    @Test
    fun `save several payment document by custom repository in one transaction with rollback test`() {
        val orderNumber = "CR_6"
        val orderDate = LocalDate.now()
        val count = 10

        assertThrows<RuntimeException> {
            service.saveSeveralDataInOneTransactionWithRollback(count, orderNumber, orderDate)
        }
        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(0)
    }

    @Test
    fun `save several payment document with copy saver in one transaction test`() {
        val orderNumber = "CR_7"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveSeveralDataWithCopySaver(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count+1)
    }

    @Test
    fun `save several payment document in two transaction with required new rollback test`() {
        val orderNumber = "CR_8"
        val orderDate = LocalDate.now()
        val count = 10

        assertThrows<RuntimeException> {
            service.saveSeveralDataInTwoTransactionWithRequiredNewAndException(count, orderNumber, orderDate)
        }
        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count)
    }

    @Test
    fun `save several payment document in two transaction with required new and incorrect data test`() {
        val orderNumber = "CR_9"
        val orderDate = LocalDate.now()
        val count = 10

        assertThrows<RuntimeException> {
            service.saveSeveralDataInTwoTransactionWithRequiredNewAndIncorrectData(count, orderNumber, orderDate)
        }
        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count)
    }

    @Test
    fun `save several payment document with manual required new transaction test`() {
        val orderNumber = "CR_10"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveSeveralDataInManualTransaction(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count+1)
    }

    @Test
    fun `save several payment document in required new transaction test`() {
        val orderNumber = "CR_11"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveSeveralDataInTwoTransactionWithRequiredNew(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count*2+1)
    }

    @Test
    fun `save several payment document with save all by copy saver test`() {
        val orderNumber = "CR_12"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveSeveralDataWithSaveAllByCopy(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count+1)
    }

    @Test
    fun `save several payment document with save all by copy saver in required one transaction test`() {
        val orderNumber = "CR_13"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveSeveralDataWithSaveAllByCopyInOneTransaction(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count*2+1)
    }

    @Test
    fun `save several payment document with save all by copy saver in required new transaction test`() {
        val orderNumber = "CR_14"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveSeveralDataWithSaveAllByCopyInNewTransaction(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count*2+1)
    }

    @Test
    fun `save several payment document with concurrent test`() {
        val orderNumber = "CR_15"
        val orderDate = LocalDate.now()
        val count = 21

        service.saveSeveralDataWithConcurrentByCopy(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count+1)
    }

    @Test
    fun `save several payment document with concurrent and exception test`() {
        val orderNumber = "CR_16"
        val orderDate = LocalDate.now()
        val count = 21
        val connections = (dataSource as HikariDataSource).hikariPoolMXBean.activeConnections

        assertThrows<RuntimeException> {
            service.saveDataWithConcurrentByCopyAndException(count, orderNumber, orderDate)
        }

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        val connectionsUpd = (dataSource as HikariDataSource).hikariPoolMXBean.activeConnections
        Assertions.assertThat(savedPd.size).isEqualTo(0)
        Assertions.assertThat(connections).isEqualTo(connectionsUpd)
    }

}

