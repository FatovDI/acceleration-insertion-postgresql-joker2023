package com.example.postgresqlinsertion.logic.service

import com.example.postgresqlinsertion.batchinsertion.api.SqlHelper
import com.example.postgresqlinsertion.batchinsertion.api.factory.BatchInsertionByEntityFactory
import com.example.postgresqlinsertion.batchinsertion.api.factory.BatchInsertionByPropertyFactory
import com.example.postgresqlinsertion.batchinsertion.api.factory.SaverType
import com.example.postgresqlinsertion.batchinsertion.utils.getRandomString
import com.example.postgresqlinsertion.batchinsertion.utils.logger
import com.example.postgresqlinsertion.logic.entity.AccountEntity
import com.example.postgresqlinsertion.logic.entity.CurrencyEntity
import com.example.postgresqlinsertion.logic.entity.PaymentDocumentEntity
import com.example.postgresqlinsertion.logic.repository.AccountRepository
import com.example.postgresqlinsertion.logic.repository.CurrencyRepository
import com.example.postgresqlinsertion.logic.repository.PaymentDocumentRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.transaction.Transactional
import kotlin.random.Random
import kotlin.reflect.KMutableProperty1

@Service
class PaymentDocumentService(
    @Value("\${batch_insertion.batch_size}")
    private val batchSize: String,
    private val accountRepo: AccountRepository,
    private val currencyRepo: CurrencyRepository,
    private val paymentDocumentRepo: PaymentDocumentRepository,
    private val sqlHelper: SqlHelper,
    private val pdBatchByEntitySaverFactory: BatchInsertionByEntityFactory<PaymentDocumentEntity>,
    private val pdBatchByPropertySaverFactory: BatchInsertionByPropertyFactory<PaymentDocumentEntity>,
) {

    private val log by logger()

    fun saveByCopy(count: Int) {
        val listId = sqlHelper.nextIdList(count)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val bathSizeInt = batchSize.toInt()

        log.info("start collect data for copy saver $count at ${LocalDateTime.now()}")

        pdBatchByEntitySaverFactory.getSaver(SaverType.COPY).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(listId[i], currencies.random(), accounts.random()))
                if (i != 0 && i % bathSizeInt == 0) {
                    log.info("save batch insertion $bathSizeInt by copy method at ${LocalDateTime.now()}")
                    saver.saveData()
                    saver.commit()
                }
            }
            saver.saveData()
            log.info("start last commit data by copy method $count to DB at ${LocalDateTime.now()}")
            saver.commit()
        }


        log.info("end save data by copy method $count at ${LocalDateTime.now()}")

    }

    fun saveByCopyWithTransaction(count: Int) {

        val listId = sqlHelper.nextIdList(count)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val bathSizeInt = batchSize.toInt()

        log.info("start collect data for copy saver with transaction $count at ${LocalDateTime.now()}")

        pdBatchByEntitySaverFactory.getSaver(SaverType.COPY).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(listId[i], currencies.random(), accounts.random()))
                if (i != 0 && i % bathSizeInt == 0) {
                    log.info("save batch insertion $bathSizeInt by copy method with transaction at ${LocalDateTime.now()}")
                    saver.saveData()
                }
            }
            saver.saveData()
            log.info("start commit data by copy method with transaction $count to DB at ${LocalDateTime.now()}")
            saver.commit()
        }

        log.info("end save data by copy method with transaction $count at ${LocalDateTime.now()}")
    }

    fun saveByCopyAndKPropertyWithTransaction(count: Int) {

        val listId = sqlHelper.nextIdList(count)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val bathSizeInt = batchSize.toInt()
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, String?>()

        log.info("start collect data for copy saver by property with transaction $count at ${LocalDateTime.now()}")

        pdBatchByPropertySaverFactory.getSaver(SaverType.COPY).use { saver ->
            for (i in 0 until count) {
                fillRandomDataByKProperty(listId[i], currencies.random(), accounts.random(), data)
                saver.addDataForSave(data)
                if (i != 0 && i % bathSizeInt == 0) {
                    log.info("save batch insertion $bathSizeInt by copy method by property with transaction at ${LocalDateTime.now()}")
                    saver.saveData(data.keys)
                }
            }
            saver.saveData(data.keys)
            log.info("start commit data by copy method by property with transaction $count to DB at ${LocalDateTime.now()}")
            saver.commit()
        }

        log.info("end save data by copy method by property with transaction $count at ${LocalDateTime.now()}")
    }

    fun saveByCopyViaFile(count: Int) {
        val listId = sqlHelper.nextIdList(count)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start creation file $count at ${LocalDateTime.now()}")

        pdBatchByEntitySaverFactory.getSaver(SaverType.COPY_VIA_FILE).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(listId[i], currencies.random(), accounts.random()))
            }

            log.info("start save file $count to DB at ${LocalDateTime.now()}")

            saver.saveData()
            saver.commit()
        }

        log.info("end save file $count at ${LocalDateTime.now()}")

    }

    fun saveByCopyAnpPropertyViaFile(count: Int) {
        val listId = sqlHelper.nextIdList(count)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, String?>()

        log.info("start creation file by property $count at ${LocalDateTime.now()}")

        pdBatchByPropertySaverFactory.getSaver(SaverType.COPY_VIA_FILE).use { saver ->
            for (i in 0 until count) {
                fillRandomDataByKProperty(listId[i], currencies.random(), accounts.random(), data)
                saver.addDataForSave(data)
            }

            log.info("start save file by property $count to DB at ${LocalDateTime.now()}")

            saver.saveData(data.keys)
            saver.commit()
        }

        log.info("end save file by property $count at ${LocalDateTime.now()}")

    }

    fun saveByInsert(count: Int) {
        val listId = sqlHelper.nextIdList(count)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val bathSizeInt = batchSize.toInt()

        log.info("start collect insertion $count at ${LocalDateTime.now()}")

        pdBatchByEntitySaverFactory.getSaver(SaverType.INSERT).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(listId[i], currencies.random(), accounts.random()))
                if (i != 0 && i % bathSizeInt == 0) {
                    log.info("save batch insertion $bathSizeInt at ${LocalDateTime.now()}")
                    saver.saveData()
                    saver.commit()
                }
            }
            saver.saveData()
            log.info("start commit last insert collection $count to DB at ${LocalDateTime.now()}")
            saver.commit()
        }

        log.info("end save insert collection $count at ${LocalDateTime.now()}")

    }

    fun saveByInsertWithTransaction(count: Int) {
        val listId = sqlHelper.nextIdList(count)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val bathSizeInt = batchSize.toInt()

        log.info("start collect insertion $count with transaction at ${LocalDateTime.now()}")

        pdBatchByEntitySaverFactory.getSaver(SaverType.INSERT).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(listId[i], currencies.random(), accounts.random()))
                if (i != 0 && i % bathSizeInt == 0) {
                    log.info("save batch insertion $bathSizeInt with transaction at ${LocalDateTime.now()}")
                    saver.saveData()
                }
            }
            saver.saveData()
            log.info("start commit insert collection $count with transaction at ${LocalDateTime.now()}")
            saver.commit()
        }

        log.info("end save insert collection $count with transaction at ${LocalDateTime.now()}")

    }

    fun saveByInsertAndPropertyWithTransaction(count: Int) {
        val listId = sqlHelper.nextIdList(count)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val bathSizeInt = batchSize.toInt()
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, String?>()

        log.info("start collect insertion $count by property with transaction at ${LocalDateTime.now()}")

        pdBatchByPropertySaverFactory.getSaver(SaverType.INSERT).use { saver ->
            for (i in 0 until count) {
                fillRandomDataByKProperty(listId[i], currencies.random(), accounts.random(), data)
                saver.addDataForSave(data)
                if (i != 0 && i % bathSizeInt == 0) {
                    log.info("save batch insertion $bathSizeInt by property with transaction at ${LocalDateTime.now()}")
                    saver.saveData(data.keys)
                }
            }
            saver.saveData(data.keys)
            log.info("start commit insert collection $count by property with transaction at ${LocalDateTime.now()}")
            saver.commit()
        }

        log.info("end save insert collection $count by property with transaction at ${LocalDateTime.now()}")

    }

    fun saveByInsertWithDropIndex(count: Int) {
        val listId = sqlHelper.nextIdList(count)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val bathSizeInt = batchSize.toInt()

        log.info("start drop index before insertion $count at ${LocalDateTime.now()}")

        val scriptForCreateIndexes = sqlHelper.dropIndex(PaymentDocumentEntity::class)

        log.info("start collect insertion with drop index $count at ${LocalDateTime.now()}")

        pdBatchByEntitySaverFactory.getSaver(SaverType.INSERT).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(listId[i], currencies.random(), accounts.random()))
                if (i != 0 && i % bathSizeInt == 0) {
                    log.info("save batch insertion with drop index $bathSizeInt at ${LocalDateTime.now()}")
                    saver.saveData()
                    saver.commit()
                }
            }

            saver.saveData()
            log.info("start save insert collection with drop index $count to DB at ${LocalDateTime.now()}")
            saver.commit()
        }

        log.info("end save insert collection with drop index $count at ${LocalDateTime.now()}")

        sqlHelper.executeScript(scriptForCreateIndexes)

        log.info("stop drop index after insertion $count at ${LocalDateTime.now()}")

    }

    @Transactional
    fun saveBySpring(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start save $count via spring at ${LocalDateTime.now()}")

        for (i in 0 until count) {
            paymentDocumentRepo.save(getRandomEntity(null, currencies.random(), accounts.random()))
        }

        log.info("end save $count via spring at ${LocalDateTime.now()}")

    }

    fun findAllByOrderNumberAndOrderDate(orderNumber: String, orderDate: LocalDate): List<PaymentDocumentEntity> {
        return paymentDocumentRepo.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
    }

    private fun getRandomEntity(id: Long?, cur: CurrencyEntity, account: AccountEntity): PaymentDocumentEntity {
        return PaymentDocumentEntity(
            orderDate = LocalDate.now(),
            orderNumber = getRandomString(10),
            amount = BigDecimal.valueOf(Random.nextDouble()),
            cur = cur,
            expense = Random.nextBoolean(),
            account = account,
            paymentPurpose = getRandomString(100),
            prop10 = getRandomString(10),
            prop15 = getRandomString(15),
            prop20 = getRandomString(20),
        ).apply { this.id = id }
    }

    private fun fillRandomDataByKProperty(
        id: Long?,
        cur: CurrencyEntity,
        account: AccountEntity,
        data: MutableMap<KMutableProperty1<PaymentDocumentEntity, *>, String?>
    ) {
        data[PaymentDocumentEntity::id] = id?.toString()
        data[PaymentDocumentEntity::orderDate] = LocalDate.now().toString()
        data[PaymentDocumentEntity::orderNumber] = getRandomString(10)
        data[PaymentDocumentEntity::amount] = BigDecimal.valueOf(Random.nextDouble()).toString()
        data[PaymentDocumentEntity::cur] = cur.code
        data[PaymentDocumentEntity::expense] = Random.nextBoolean().toString()
        data[PaymentDocumentEntity::account] = account.id?.toString()
        data[PaymentDocumentEntity::paymentPurpose] = getRandomString(100)
        data[PaymentDocumentEntity::prop10] = getRandomString(10)
        data[PaymentDocumentEntity::prop15] = getRandomString(15)
        data[PaymentDocumentEntity::prop20] = getRandomString(20)
    }
}