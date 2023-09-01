package com.example.postgresqlinsertion.logic.repository

import com.example.postgresqlinsertion.logic.service.PaymentDocumentService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.properties"])
@Execution(ExecutionMode.CONCURRENT)
internal class PaymentDocumentCustomRepositoryTest {

    // todo create tests

    @Autowired
    lateinit var service: PaymentDocumentService

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `payment document custom repository test 1`() {
        service.saveByCopyViaSpring(7)
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `payment document custom repository test 2`() {
        service.saveByCopyViaSpring(10)
    }

}