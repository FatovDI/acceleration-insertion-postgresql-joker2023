package com.example.postgresqlinsertion.batchinsertion.benchmark

import com.example.postgresqlinsertion.batchinsertion.benchmark.blackhole.ConnectionBlackhole
import com.example.postgresqlinsertion.batchinsertion.getTableName
import com.example.postgresqlinsertion.batchinsertion.impl.processor.PostgresBatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.impl.processor.PostgresBatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.logic.entity.*
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import java.math.BigDecimal
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.reflect.KMutableProperty1

@State(Scope.Benchmark)
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 2)
class ProcessorBenchmark {

    @Benchmark
    fun saveDataByKotlinProperty_2_000_000(bh: Blackhole) {
        saveDataByKotlinProperty(2000000, bh)
    }

    @Benchmark
    fun saveDataByJpamodelgen_2_000_000(bh: Blackhole) {
        saveDataByJpamodelgen(2000000, bh)
    }

    @Benchmark
    fun saveDataWithReflection_2_000_000(bh: Blackhole) {
        saveDataByReflection(2000000, bh)
    }

    fun saveDataByReflection(count: Int, bh: Blackhole) {
        val processor = PostgresBatchInsertionByEntityProcessor()

        for (i in 1..count) {
            val data = PaymentDocumentEntity(
                account = AccountEntity().apply { id = 1 },
                expense = false,
                amount = BigDecimal("10.11"),
                cur = CurrencyEntity(code = "RUB"),
                orderDate = LocalDate.parse("2023-01-01"),
                orderNumber = "123",
                prop20 = "1345",
                prop15 = "END",
                paymentPurpose = "paymentPurpose",
                prop10 = "prop10",
            )

            bh.consume(processor.getStringForInsert(data))
        }

        processor.insertDataToDataBase(PaymentDocumentEntity::class, listOf(), ConnectionBlackhole(bh))
    }

    fun saveDataByKotlinProperty(count: Int, bh: Blackhole) {
        val nullValue = "NULL"
        val processor = PostgresBatchInsertionByPropertyProcessor()

        val data = mutableMapOf<KMutableProperty1<out BaseEntity, *>, String?>()

        for (i in 1..count) {
            data[PaymentDocumentEntity::account] = "1"
            data[PaymentDocumentEntity::amount] = "10.11"
            data[PaymentDocumentEntity::expense] = "true"
            data[PaymentDocumentEntity::cur] = "RUB"
            data[PaymentDocumentEntity::orderDate] = "2023-01-01"
            data[PaymentDocumentEntity::orderNumber] = "123"
            data[PaymentDocumentEntity::prop20] = "1345"
            data[PaymentDocumentEntity::prop15] = "END"
            data[PaymentDocumentEntity::paymentPurpose] = "paymentPurpose"
            data[PaymentDocumentEntity::prop10] = "prop10"

            bh.consume(processor.getStringForInsert(data, nullValue))
        }

        processor.insertDataToDataBase(PaymentDocumentEntity::class, data.keys, listOf(), ConnectionBlackhole(bh))

    }

    fun saveDataByJpamodelgen(count: Int, bh: Blackhole) {
        val nullValue = "NULL"
        val processor = PostgresBatchInsertionByPropertyProcessor()

        val data = mutableMapOf<String, String>()

        for (i in 1..count) {
            data[PaymentDocumentEntity_.ACCOUNT] = "1"
            data[PaymentDocumentEntity_.AMOUNT] = "10.11"
            data[PaymentDocumentEntity_.EXPENSE] = "true"
            data[PaymentDocumentEntity_.CUR] = "RUB"
            data[PaymentDocumentEntity_.ORDER_DATE] = "2023-01-01"
            data[PaymentDocumentEntity_.ORDER_NUMBER] = "123"
            data[PaymentDocumentEntity_.PROP20] = "1345"
            data[PaymentDocumentEntity_.PROP15] = "END"
            data[PaymentDocumentEntity_.PAYMENT_PURPOSE] = "paymentPurpose"
            data[PaymentDocumentEntity_.PROP10] = "prop10"

            bh.consume(processor.getStringForInsert(data.values, nullValue))
        }

        processor.insertDataToDataBase(
            getTableName(PaymentDocumentEntity::class),
            data.keys.joinToString(","),
            listOf(),
            ConnectionBlackhole(bh)
        )

    }
}
