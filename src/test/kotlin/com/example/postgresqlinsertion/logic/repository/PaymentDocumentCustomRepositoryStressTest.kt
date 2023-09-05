package com.example.postgresqlinsertion.logic.repository

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
@Execution(ExecutionMode.CONCURRENT)
internal class PaymentDocumentCustomRepositoryStressTest {

    @Autowired
    lateinit var service: PaymentDocumentTestService

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `save concurrent CR_1 count 10 stress test`() {
        val orderNumber = "CR_1"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveByCopyViaSpring(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count)
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `save concurrent CR_1 count 100000 stress test`() {
        val orderNumber = "CR_1"
        val orderDate = LocalDate.now()
        val count = 100000

        service.saveByCopyViaSpring(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count + 10)
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `save concurrent CR_2 count 10 stress test`() {
        val orderNumber = "CR_2"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveByCopyViaSpring(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count)
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `save concurrent CR_2 count 100000 stress test`() {
        val orderNumber = "CR_2"
        val orderDate = LocalDate.now()
        val count = 100000

        service.saveByCopyViaSpring(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count + 10)
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `save concurrent CR_3 count 10 stress test`() {
        val orderNumber = "CR_3"
        val orderDate = LocalDate.now()
        val count = 10

        service.saveByCopyViaSpring(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count)
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `save concurrent CR_3 count 100000 stress test`() {
        val orderNumber = "CR_3"
        val orderDate = LocalDate.now()
        val count = 100000

        service.saveByCopyViaSpring(count, orderNumber, orderDate)

        val savedPd = service.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
        Assertions.assertThat(savedPd.size).isEqualTo(count + 10)
    }

}

