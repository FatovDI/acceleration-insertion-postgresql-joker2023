package com.example.postgresqlinsertion.service.batchinsertion

import com.example.postgresqlinsertion.entity.AccountEntity
import com.example.postgresqlinsertion.entity.CurrencyEntity
import com.example.postgresqlinsertion.entity.PaymentDocumentEntity
import com.example.postgresqlinsertion.service.batchinsertion.api.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.service.batchinsertion.impl.PostgresBatchInsertionByEntityProcessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.io.File
import java.io.FileReader
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.sql.DataSource


@SpringBootTest(classes = [PostgresBatchInsertionByEntityProcessor::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = ["classpath:application-test.properties"])
@EnableAutoConfiguration
internal class PostgresBatchInsertionByEntityProcessorTest {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var em: EntityManager

    @Autowired
    lateinit var processor: BatchInsertionByEntityProcessor

    lateinit var conn: Connection

    @BeforeEach
    fun setUp() {
        conn = dataSource.connection
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    fun `update saved data via entity`(params: Pair<String, String>) {
        val paymentPurpose = params.first
        val prop10 = params.second + "8"
        val accountId = em.createNativeQuery("select id from account limit 1").singleResult.toString()
        val data = PaymentDocumentEntity(
            prop15 = "END",
            paymentPurpose = null,
            prop10 = prop10,
        )
        val dataForInsert = mutableListOf<String>()
        dataForInsert.add(processor.getStringForInsert(data))
        processor.insertDataToDataBase(clazz = PaymentDocumentEntity::class, data = dataForInsert, conn = conn)
        val savedDoc =
            em.createNativeQuery("select id, payment_purpose  from payment_document where prop_10 = '$prop10'").resultList as List<Array<Any>>
        val pdId = savedDoc.first()[0].toString().toLong()
        assertThat(pdId).isNotNull
        assertThat(savedDoc.first()[1]).isNull()

        val dataForUpdate = mutableListOf<String>()
        val dataUpdate = PaymentDocumentEntity(
            account = AccountEntity().apply { id = accountId.toLong() },
            prop15 = "END",
            paymentPurpose = paymentPurpose,
            prop10 = prop10,
        ).apply { id = pdId }
        dataForUpdate.add(processor.getStringForUpdate(dataUpdate))
        processor.updateDataToDataBase(clazz = PaymentDocumentEntity::class, data = dataForUpdate, conn = conn)

        val updatedDoc =
            em.createNativeQuery("select account_id, prop_15, payment_purpose  from payment_document where prop_10 = '$prop10'").resultList as List<Array<Any>>
        assertThat(updatedDoc.first()[0].toString()).isEqualTo(accountId)
        assertThat(updatedDoc.first()[1]).isEqualTo("END")
        assertThat(updatedDoc.first()[2]).isEqualTo(params.first)
    }

    @Test
    fun `save several entity data via insert method`() {
        val cur = em.createNativeQuery("select code from currency limit 1").singleResult.toString()
        val dataForInsert = mutableListOf<String>()

        getTestData().forEach {
            val data = PaymentDocumentEntity(
                prop15 = "NEW",
                cur = CurrencyEntity(code = cur),
                paymentPurpose = it.first,
                prop10 = it.second,
            )
            dataForInsert.add(processor.getStringForInsert(data))
        }
        processor.insertDataToDataBase(clazz = PaymentDocumentEntity::class, data = dataForInsert, conn = conn)

        val savedDoc =
            em.createNativeQuery("select payment_purpose, prop_15, prop_10, cur  from payment_document where prop_15 = 'NEW' and cur = '$cur'").resultList as List<Array<Any>>
        assertThat(savedDoc.size).isEqualTo(4)
        getTestData().forEachIndexed { index, pair ->
            assertThat(savedDoc[index][0]).isEqualTo(pair.first)
            assertThat(savedDoc[index][1]).isEqualTo("NEW")
            assertThat(savedDoc[index][2]).isEqualTo(pair.second)
            assertThat(savedDoc[index][3].toString()).isEqualTo(cur)
        }
    }

    @Test
    fun `save all entity data via copy method`() {
        val file = File(this::class.java.getResource("").file, "/PD_Test.csv")
        val writer = file.bufferedWriter()
        val prop10 = "7171"
        val accountId = em.createNativeQuery("select id from account limit 1").singleResult.toString()
        val data = PaymentDocumentEntity(
            account = AccountEntity().apply { id = accountId.toLong() },
            expense = false,
            amount = BigDecimal("10.11"),
            cur = CurrencyEntity(code = "RUB"),
            orderDate = LocalDate.parse("2023-01-01"),
            orderNumber = "123",
            prop20 = "1345",
            prop15 = "END",
            paymentPurpose = null,
            prop10 = prop10,
        )

        processor.addDataForCreate(data, writer)
        writer.close()
        processor.saveToDataBaseByCopyMethod(clazz = PaymentDocumentEntity::class, from = FileReader(file), conn = conn)

        val savedDoc = em.createNativeQuery(
            """
                select
                    account_id,
                    expense,
                    cur,
                    amount,
                    order_date,
                    order_number,
                    prop_20,
                    prop_15,
                    payment_purpose,
                    prop_10
                from payment_document where prop_10 = '$prop10'
            """.trimIndent()
        ).resultList as List<Array<Any>>
        assertThat(savedDoc.first()[0].toString()).isEqualTo(accountId)
        assertThat(savedDoc.first()[1]).isEqualTo(false)
        assertThat(savedDoc.first()[2]).isEqualTo("RUB")
        assertThat(savedDoc.first()[3]).isEqualTo(BigDecimal("10.11"))
        assertThat(savedDoc.first()[4]).isEqualTo(Date.valueOf("2023-01-01"))
        assertThat(savedDoc.first()[5]).isEqualTo("123")
        assertThat(savedDoc.first()[6]).isEqualTo("1345")
        assertThat(savedDoc.first()[7]).isEqualTo("END")
        assertThat(savedDoc.first()[8]).isNull()
        assertThat(savedDoc.first()[9]).isEqualTo(prop10)
    }

    @Test
    fun `save several entity data via copy method`() {
        val file = File(this::class.java.getResource("").file, "/PD_Test.csv")
        val writer = file.bufferedWriter()
        val prop20 = "77778778"

        getTestData().forEach {
            val data = PaymentDocumentEntity(
                prop15 = "END",
                prop20 = prop20,
                paymentPurpose = it.first,
                prop10 = it.second,
            )
            processor.addDataForCreate(data, writer)
        }
        writer.close()
        processor.saveToDataBaseByCopyMethod(clazz = PaymentDocumentEntity::class, from = FileReader(file), conn = conn)

        val savedDoc =
            em.createNativeQuery("select payment_purpose, prop_15, prop_10, prop_20  from payment_document where prop_20 = '$prop20'").resultList as List<Array<Any>>
        assertThat(savedDoc.size).isEqualTo(4)
        getTestData().forEachIndexed { index, pair ->
            assertThat(savedDoc[index][0]).isEqualTo(pair.first)
            assertThat(savedDoc[index][1]).isEqualTo("END")
            assertThat(savedDoc[index][2]).isEqualTo(pair.second)
            assertThat(savedDoc[index][3]).isEqualTo(prop20)
        }
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    fun `save entity via copy method with all special symbols`(params: Pair<String, String>) {
        val file = File(this::class.java.getResource("").file, "/PD_Test.csv")
        val writer = file.bufferedWriter()
        val paymentPurpose = params.first
        val prop10 = params.second + 1
        val data = PaymentDocumentEntity(
            prop15 = "END",
            paymentPurpose = paymentPurpose,
            prop10 = prop10,
        )

        processor.addDataForCreate(data = data, writer = writer)
        writer.close()
        processor.saveToDataBaseByCopyMethod(clazz = PaymentDocumentEntity::class, from = FileReader(file), conn = conn)

        val savedDoc =
            em.createNativeQuery("select payment_purpose, prop_15, prop_10  from payment_document where prop_10 = '$prop10'").resultList as List<Array<Any>>
        assertThat(savedDoc.first()[0]).isEqualTo(paymentPurpose)
        assertThat(savedDoc.first()[1]).isEqualTo("END")
        assertThat(savedDoc.first()[2]).isEqualTo(prop10)
    }

    companion object {
        @JvmStatic
        fun getTestData(): List<Pair<String, String>> {
            return listOf(
                Pair("бла бла", "222"),
                Pair("бла бла | бла блабла", "333"),
                Pair("б`л~а !б@л#а№;ж\$s%u ^s p &l? z* (d)- _s+= /W\\|{we}[ct]a,r<cs.>w's", "444"),
                Pair("бла\b бла \n бла \r бла \tбла бла", "555")
            )
        }

    }

}
