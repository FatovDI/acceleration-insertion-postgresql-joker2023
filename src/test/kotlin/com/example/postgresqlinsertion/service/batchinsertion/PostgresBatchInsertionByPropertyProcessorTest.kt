package com.example.postgresqlinsertion.service.batchinsertion

import com.example.postgresqlinsertion.entity.BaseEntity
import com.example.postgresqlinsertion.entity.PaymentDocumentEntity
import com.example.postgresqlinsertion.service.batchinsertion.api.BatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.service.batchinsertion.impl.PostgresBatchInsertionByPropertyProcessor
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.util.PSQLException
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
import javax.persistence.EntityManager
import javax.sql.DataSource
import kotlin.reflect.KMutableProperty1


@SpringBootTest(classes = [PostgresBatchInsertionByPropertyProcessor::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = ["classpath:application-test.properties"])
@EnableAutoConfiguration
internal class PostgresBatchInsertionByPropertyProcessorTest {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var em: EntityManager

    @Autowired
    lateinit var processor: BatchInsertionByPropertyProcessor

    lateinit var conn: Connection

    private val delimiter = "|"
    private val nullValue = "NULL"

    @BeforeEach
    fun setUp() {
        conn = dataSource.connection
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    fun `update saved data`(params: Pair<String, String>) {
        val paymentPurpose = params.first
        val prop10 = params.second + "7"
        val accountId = em.createNativeQuery("select id from account limit 1").singleResult.toString()
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::prop10 to "END",
            PaymentDocumentEntity::paymentPurpose to null,
            PaymentDocumentEntity::prop10 to prop10,
        )
        val dataForInsert = mutableListOf<String>()
        dataForInsert.add(processor.getStringForInsert(data = data, nullValue = nullValue))
        processor.insertDataToDataBase(
            clazz = PaymentDocumentEntity::class,
            columns = data.keys,
            data = dataForInsert,
            conn = conn
        )
        val savedDoc =
            em.createNativeQuery("select id, payment_purpose  from payment_document where prop_10 = '$prop10'").resultList as List<Array<Any>>
        val pdId = savedDoc.first()[0].toString().toLong()
        assertThat(pdId).isNotNull
        assertThat(savedDoc.first()[1]).isNull()

        val dataForUpdate = mutableListOf<String>()
        val dataUpdate = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::account to accountId,
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::paymentPurpose to paymentPurpose,
            PaymentDocumentEntity::prop10 to prop10,
        )
        dataForUpdate.add(processor.getStringForUpdate(data = dataUpdate, id = pdId, nullValue = nullValue))
        processor.updateDataToDataBase(
            clazz = PaymentDocumentEntity::class,
            columns = dataUpdate.keys,
            data = dataForUpdate,
            conn = conn
        )

        val updatedDoc =
            em.createNativeQuery("select account_id, prop_15, payment_purpose  from payment_document where prop_10 = '$prop10'").resultList as List<Array<Any>>
        assertThat(updatedDoc.first()[0].toString()).isEqualTo(accountId)
        assertThat(updatedDoc.first()[1]).isEqualTo("END")
        assertThat(updatedDoc.first()[2]).isEqualTo(params.first)
    }

    @Test
    fun `save several data via insert method`() {
        val cur = em.createNativeQuery("select code from currency limit 1").singleResult.toString()
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::cur to cur,
            PaymentDocumentEntity::orderDate to "2022-01-01",
        )
        val dataForInsert = mutableListOf<String>()

        getTestData().forEach {
            data[PaymentDocumentEntity::paymentPurpose] = it.first
            data[PaymentDocumentEntity::prop10] = it.second
            dataForInsert.add(processor.getStringForInsert(data = data, nullValue = nullValue))
        }
        processor.insertDataToDataBase(
            clazz = PaymentDocumentEntity::class,
            columns = data.keys,
            data = dataForInsert,
            conn = conn
        )

        val savedDoc =
            em.createNativeQuery("select payment_purpose, prop_15, prop_10, cur  from payment_document where order_date = '2022-01-01' and cur = '$cur'").resultList as List<Array<Any>>
        assertThat(savedDoc.size).isEqualTo(4)
        getTestData().forEachIndexed { index, pair ->
            assertThat(savedDoc[index][0]).isEqualTo(pair.first)
            assertThat(savedDoc[index][1]).isEqualTo("END")
            assertThat(savedDoc[index][2]).isEqualTo(pair.second)
            assertThat(savedDoc[index][3].toString()).isEqualTo(cur)
        }
    }

    @Test
    fun `save data with null value via insert method`() {
        val prop10 = "777"
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::account to null,
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::paymentPurpose to null,
            PaymentDocumentEntity::prop10 to prop10,
        )
        val dataForInsert = mutableListOf<String>()

        dataForInsert.add(processor.getStringForInsert(data = data, nullValue = nullValue))
        processor.insertDataToDataBase(
            clazz = PaymentDocumentEntity::class,
            columns = data.keys,
            data = dataForInsert,
            conn = conn
        )

        val savedDoc =
            em.createNativeQuery("select payment_purpose, prop_15, prop_10, account_id  from payment_document where prop_10 = '$prop10'").resultList as List<Array<Any>>
        assertThat(savedDoc.first()[0]).isNull()
        assertThat(savedDoc.first()[1]).isEqualTo("END")
        assertThat(savedDoc.first()[2]).isEqualTo(prop10)
        assertThat(savedDoc.first()[3]).isNull()
    }

    @Test
    fun `save data with incorrect value via insert method`() {
        val prop10 = "777"
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::account to "1",
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::paymentPurpose to null,
            PaymentDocumentEntity::prop10 to prop10,
        )
        val dataForInsert = mutableListOf<String>()

        dataForInsert.add(processor.getStringForInsert(data = data, nullValue = nullValue))
        assertThatThrownBy {
            processor.insertDataToDataBase(
                clazz = PaymentDocumentEntity::class,
                columns = data.keys,
                data = dataForInsert,
                conn = conn
            )
        }.isInstanceOf(PSQLException::class.java)
    }

    @Test
    fun `save incorrect data via copy method`() {
        val file = File(this::class.java.getResource("").file, "/PD_Test.csv")
        val writer = file.bufferedWriter()
        val prop10 = "666"
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::account to "1",
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::paymentPurpose to null,
            PaymentDocumentEntity::prop10 to prop10,
        )

        processor.addDataForCreate(data = data, writer = writer, delimiter = delimiter, nullValue = nullValue)
        writer.close()
        assertThatThrownBy {
            processor.saveToDataBaseByCopyMethod(
                clazz = PaymentDocumentEntity::class,
                columns = data.keys,
                delimiter = delimiter,
                nullValue = nullValue,
                from = FileReader(file),
                conn = conn
            )
        }.isInstanceOf(PSQLException::class.java)
    }

    @Test
    fun `save null data via copy method`() {
        val file = File(this::class.java.getResource("").file, "/PD_Test.csv")
        val writer = file.bufferedWriter()
        val prop10 = "656"
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::account to null,
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::paymentPurpose to null,
            PaymentDocumentEntity::prop10 to prop10,
        )

        processor.addDataForCreate(data = data, writer = writer, delimiter = delimiter, nullValue = nullValue)
        writer.close()
        processor.saveToDataBaseByCopyMethod(
            clazz = PaymentDocumentEntity::class,
            columns = data.keys,
            delimiter = delimiter,
            nullValue = nullValue,
            from = FileReader(file),
            conn = conn
        )

        val savedDoc =
            em.createNativeQuery("select payment_purpose, prop_15, prop_10, account_id  from payment_document where prop_10 = '$prop10'").resultList as List<Array<Any>>
        assertThat(savedDoc.first()[0]).isNull()
        assertThat(savedDoc.first()[1]).isEqualTo("END")
        assertThat(savedDoc.first()[2]).isEqualTo(prop10)
        assertThat(savedDoc.first()[3]).isNull()
    }

    @Test
    fun `save all data via copy method`() {
        val file = File(this::class.java.getResource("").file, "/PD_Test.csv")
        val writer = file.bufferedWriter()
        val prop10 = "717"
        val accountId = em.createNativeQuery("select id from account limit 1").singleResult.toString()
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::account to accountId,
            PaymentDocumentEntity::amount to "10.11",
            PaymentDocumentEntity::expense to "true",
            PaymentDocumentEntity::cur to "RUB",
            PaymentDocumentEntity::orderDate to "2023-01-01",
            PaymentDocumentEntity::orderNumber to "123",
            PaymentDocumentEntity::prop20 to "1345",
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::paymentPurpose to null,
            PaymentDocumentEntity::prop10 to prop10,
        )

        processor.addDataForCreate(data = data, writer = writer, delimiter = delimiter, nullValue = nullValue)
        writer.close()
        processor.saveToDataBaseByCopyMethod(
            clazz = PaymentDocumentEntity::class,
            columns = data.keys,
            delimiter = delimiter,
            nullValue = nullValue,
            from = FileReader(file),
            conn = conn
        )

        val savedDoc = em.createNativeQuery(
            """
                select
                    account_id,
                    amount,
                    expense,
                    cur,
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
        assertThat(savedDoc.first()[1]).isEqualTo(BigDecimal("10.11"))
        assertThat(savedDoc.first()[2]).isEqualTo(true)
        assertThat(savedDoc.first()[3]).isEqualTo("RUB")
        assertThat(savedDoc.first()[4]).isEqualTo(Date.valueOf("2023-01-01"))
        assertThat(savedDoc.first()[5]).isEqualTo("123")
        assertThat(savedDoc.first()[6]).isEqualTo("1345")
        assertThat(savedDoc.first()[7]).isEqualTo("END")
        assertThat(savedDoc.first()[8]).isNull()
        assertThat(savedDoc.first()[9]).isEqualTo(prop10)
    }

    @Test
    fun `save several data via copy method`() {
        val file = File(this::class.java.getResource("").file, "/PD_Test.csv")
        val writer = file.bufferedWriter()
        val prop20 = "7777877"
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::prop20 to "7777877",
        )

        getTestData().forEach {
            data[PaymentDocumentEntity::paymentPurpose] = it.first
            data[PaymentDocumentEntity::prop10] = it.second
            processor.addDataForCreate(data = data, writer = writer, delimiter = delimiter, nullValue = nullValue)
        }
        writer.close()
        processor.saveToDataBaseByCopyMethod(
            clazz = PaymentDocumentEntity::class,
            columns = data.keys,
            delimiter = delimiter,
            nullValue = nullValue,
            from = FileReader(file),
            conn = conn
        )

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

    @Test
    fun `save data via copy method with comma delimiter`() {
        val file = File(this::class.java.getResource("").file, "/PD_Test.csv")
        val writer = file.bufferedWriter()
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::paymentPurpose to "бла бла , бла блабла",
            PaymentDocumentEntity::prop10 to "111",
        )

        processor.addDataForCreate(data = data, writer = writer, delimiter = ",", nullValue = nullValue)
        writer.close()
        processor.saveToDataBaseByCopyMethod(
            clazz = PaymentDocumentEntity::class, columns = data.keys,
            delimiter = ",",
            nullValue = nullValue,
            from = FileReader(file),
            conn = conn
        )

        val savedDoc =
            em.createNativeQuery("select payment_purpose, prop_15, prop_10  from payment_document where prop_10 = '111'").resultList as List<Array<Any>>
        assertThat(savedDoc.first()[0]).isEqualTo("бла бла , бла блабла")
        assertThat(savedDoc.first()[1]).isEqualTo("END")
        assertThat(savedDoc.first()[2]).isEqualTo("111")
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    fun `save via copy method with all special symbols`(params: Pair<String, String>) {
        val file = File(this::class.java.getResource("").file, "/PD_Test.csv")
        val writer = file.bufferedWriter()
        val paymentPurpose = params.first
        val prop10 = params.second + "6"
        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>(
            PaymentDocumentEntity::prop15 to "END",
            PaymentDocumentEntity::paymentPurpose to paymentPurpose,
            PaymentDocumentEntity::prop10 to prop10,
        )

        processor.addDataForCreate(data = data, writer = writer, delimiter = delimiter, nullValue = nullValue)
        writer.close()
        processor.saveToDataBaseByCopyMethod(
            clazz = PaymentDocumentEntity::class, columns = data.keys,
            delimiter = delimiter,
            nullValue = nullValue,
            from = FileReader(file),
            conn = conn
        )

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
