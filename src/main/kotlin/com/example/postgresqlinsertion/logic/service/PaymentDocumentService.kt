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
import com.example.postgresqlinsertion.logic.repository.PaymentDocumentCustomRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional
import kotlin.random.Random
import kotlin.reflect.KMutableProperty1


@Service
class PaymentDocumentService(
    @Value("\${batch_insertion.batch_size}")
    private val batchSize: Int,
    private val accountRepo: AccountRepository,
    private val currencyRepo: CurrencyRepository,
    private val sqlHelper: SqlHelper,
    private val pdBatchByEntitySaverFactory: BatchInsertionByEntityFactory<PaymentDocumentEntity>,
    private val pdBatchByPropertySaverFactory: BatchInsertionByPropertyFactory<PaymentDocumentEntity>,
    private val pdCustomRepository: PaymentDocumentCustomRepository,
) {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    private val log by logger()

    fun saveByCopy(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        pdBatchByEntitySaverFactory.getSaver(SaverType.COPY).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(null, currencies.random(), accounts.random()))
            }
            saver.commit()
        }

    }

    fun saveByCopyBinary(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        pdBatchByEntitySaverFactory.getSaver(SaverType.COPY_BINARY).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(null, currencies.random(), accounts.random()))
            }
            saver.commit()
        }

    }

    fun saveByCopyAndKProperty(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, Any?>()

        log.info("start collect data for copy saver by property $count")

        pdBatchByPropertySaverFactory.getSaver(SaverType.COPY).use { saver ->
            for (i in 0 until count) {
                fillRandomDataByKProperty(null, currencies.random(), accounts.random(), data)
                saver.addDataForSave(data)
                if (i != 0 && i % batchSize == 0) {
                    log.info("save batch insertion $batchSize by copy method by property ")
                    saver.saveData(data.keys)
                }
            }
            saver.saveData(data.keys)
            log.info("start commit data by copy method by property $count to DB")
            saver.commit()
        }

        log.info("end save data by copy method by property $count")
    }

    fun saveByCopyBinaryAndKProperty(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, Any?>()

        log.info("start collect binary data for copy saver by property $count")

        pdBatchByPropertySaverFactory.getSaver(SaverType.COPY_BINARY).use { saver ->
            for (i in 0 until count) {
                fillRandomDataByKProperty(null, currencies.random(), accounts.random(), data)
                saver.addDataForSave(data)
                if (i != 0 && i % batchSize == 0) {
                    log.info("save batch insertion $batchSize by copy with binary data method by property")
                    saver.saveData(data.keys)
                }
            }
            saver.saveData(data.keys)
            log.info("start commit binary data by copy method by property $count to DB")
            saver.commit()
        }

        log.info("end save binary data by copy method by property $count")
    }

    fun saveByCopyViaFile(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        pdBatchByEntitySaverFactory.getSaver(SaverType.COPY_VIA_FILE).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(null, currencies.random(), accounts.random()))
            }
            saver.commit()
        }

    }

    fun saveByCopyViaBinaryFile(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        pdBatchByEntitySaverFactory.getSaver(SaverType.COPY_BINARY_VIA_FILE).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(null, currencies.random(), accounts.random()))
            }
            saver.commit()
        }

    }

    fun saveByCopyAnpPropertyViaFile(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, Any?>()

        log.info("start creation file by property $count")

        pdBatchByPropertySaverFactory.getSaver(SaverType.COPY_VIA_FILE).use { saver ->
            for (i in 0 until count) {
                fillRandomDataByKProperty(null, currencies.random(), accounts.random(), data)
                saver.addDataForSave(data)
            }

            log.info("start save file by property $count to DB")

            saver.saveData(data.keys)
            saver.commit()
        }

        log.info("end save file by property $count")

    }

    fun saveByCopyAnpPropertyViaBinaryFile(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, Any?>()

        log.info("start creation binary file by property $count")

        pdBatchByPropertySaverFactory.getSaver(SaverType.COPY_BINARY_VIA_FILE).use { saver ->
            for (i in 0 until count) {
                fillRandomDataByKProperty(null, currencies.random(), accounts.random(), data)
                saver.addDataForSave(data)
            }

            log.info("start save binary file by property $count to DB")

            saver.saveData(data.keys)
            saver.commit()
        }

        log.info("end save binary file by property $count")

    }

    fun saveByInsert(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        pdBatchByEntitySaverFactory.getSaver(SaverType.INSERT).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(null, currencies.random(), accounts.random()))
            }
            saver.commit()
        }

    }

    fun update(count: Int) {
        val listId = sqlHelper.getIdListForUpdate(count, PaymentDocumentEntity::class)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        pdBatchByEntitySaverFactory.getSaver(SaverType.UPDATE).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(listId[i], currencies.random(), accounts.random()))
            }
            saver.commit()
        }

    }

    fun saveByInsertAndProperty(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, Any?>()

        log.info("start collect insertion $count by property")

        pdBatchByPropertySaverFactory.getSaver(SaverType.INSERT).use { saver ->
            for (i in 0 until count) {
                fillRandomDataByKProperty(null, currencies.random(), accounts.random(), data)
                saver.addDataForSave(data)
                if (i != 0 && i % batchSize == 0) {
                    log.info("save batch insertion $batchSize by property")
                    saver.saveData(data.keys)
                }
            }
            saver.saveData(data.keys)
            log.info("start commit insert collection $count by property")
            saver.commit()
        }

        log.info("end save insert collection $count by property")

    }

    fun updateByProperty(count: Int) {
        val listId = sqlHelper.getIdListForUpdate(count, PaymentDocumentEntity::class)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, Any?>()

        log.info("start update $count by property")

        pdBatchByPropertySaverFactory.getSaver(SaverType.UPDATE).use { saver ->
            for (i in 0 until count) {
                fillRandomDataByKProperty(listId[i], currencies.random(), accounts.random(), data)
                saver.addDataForSave(data)
                if (i != 0 && i % batchSize == 0) {
                    log.info("save batch update $batchSize by property")
                    saver.saveData(data.keys)
                }
            }
            saver.saveData(data.keys)
            log.info("start commit update collection $count by property")
            saver.commit()
        }

        log.info("end update collection $count by property")

    }

    fun updateOnlyOneFieldByProperty(count: Int) {
        val listId = sqlHelper.getIdListForUpdate(count, PaymentDocumentEntity::class)
        val data = mutableMapOf<KMutableProperty1<PaymentDocumentEntity, *>, String?>()

        log.info("start update only one field $count by property")

        pdBatchByPropertySaverFactory.getSaver(SaverType.UPDATE).use { saver ->
            for (i in 0 until count) {
                data[PaymentDocumentEntity::id] = listId[i].toString()
                data[PaymentDocumentEntity::prop10] = getRandomString(10)
                saver.addDataForSave(data)
                if (i != 0 && i % batchSize == 0) {
                    log.info("save batch update only one field $batchSize by property")
                    saver.saveData(data.keys)
                }
            }
            saver.saveData(data.keys)
            log.info("start commit update only one field collection $count by property")
            saver.commit()
        }

        log.info("end update only one field collection $count by property")

    }

    fun saveByInsertWithDropIndex(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start drop index before insertion $count")

        val scriptForCreateIndexes = sqlHelper.dropIndex(PaymentDocumentEntity::class)

        pdBatchByEntitySaverFactory.getSaver(SaverType.INSERT).use { saver ->
            for (i in 0 until count) {
                saver.addDataForSave(getRandomEntity(null, currencies.random(), accounts.random()))
            }
            saver.commit()
        }

        log.info("start create index after insertion $count")

        sqlHelper.executeScript(scriptForCreateIndexes)

        log.info("stop create index after insertion $count")

    }

    @Transactional
    fun saveAllBySpring(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start saveAll $count via spring")

        (1..count)
            .map {  getRandomEntity(null, currencies.random(), accounts.random()) }
            .let {

                log.info("start call saveAll method $count via spring")

                pdCustomRepository.saveAll(it)
            }

        log.info("end saveAll $count via spring")

    }

    @Transactional
    fun saveBySpring(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start save $count by Spring")
        for (i in 0 until count) {
            pdCustomRepository.save(
                getRandomEntity(null, currencies.random(), accounts.random())
            )
        }
        log.info("end save $count by Spring")
    }

    @Transactional
    fun saveBySpringWithManualPersisting(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start save $count by Spring with manual persisting")

        for (i in 0 until count) {
            entityManager.persist(getRandomEntity(null, currencies.random(), accounts.random()))
            if (i != 0 && i % batchSize == 0) {
                log.info("save batch $batchSize by Spring with manual persisting")
                entityManager.flush()
                entityManager.clear()
            }
        }

        log.info("end save $count by Spring with manual persisting")

    }

    @Transactional
    fun saveByCopyViaSpring(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start save by copy $count via spring")

        for (i in 0 until count) {
            pdCustomRepository.saveByCopy(getRandomEntity(null, currencies.random(), accounts.random()))
        }

        log.info("end save by copy $count via spring")

    }

    @Transactional
    fun saveByCopyConcurrentViaSpring(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start save by copy concurrent $count via spring")

        for (i in 0 until count) {
            pdCustomRepository.saveByCopyConcurrent(getRandomEntity(null, currencies.random(), accounts.random()))
        }

        log.info("end save by copy concurrent $count via spring")

    }

    @Transactional
    fun saveAllByCopyViaSpring(count: Int) {
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start save all by copy $count via spring")

        (1..count)
            .map {getRandomEntity(null, currencies.random(), accounts.random())}
            .let { pdCustomRepository.saveAllByCopy(it) }

        log.info("end save all by copy $count via spring")

    }

    @Transactional
    fun updateBySpring(count: Int) {
        val listId = sqlHelper.getIdListForUpdate(count, PaymentDocumentEntity::class)
        val currencies = currencyRepo.findAll()
        val accounts = accountRepo.findAll()

        log.info("start update $count via spring")

        for (i in 0 until count) {
            pdCustomRepository.save(getRandomEntity(listId[i], currencies.random(), accounts.random()))
        }

        log.info("end update $count via spring")

    }

    fun findAllByOrderNumberAndOrderDate(orderNumber: String, orderDate: LocalDate): List<PaymentDocumentEntity> {
        return pdCustomRepository.findAllByOrderNumberAndOrderDate(orderNumber, orderDate)
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
        data: MutableMap<KMutableProperty1<PaymentDocumentEntity, *>, Any?>
    ) {
        id?.let { data[PaymentDocumentEntity::id] = it }
        data[PaymentDocumentEntity::orderDate] = LocalDate.now()
        data[PaymentDocumentEntity::orderNumber] = getRandomString(10)
        data[PaymentDocumentEntity::amount] = BigDecimal.valueOf(Random.nextDouble())
        data[PaymentDocumentEntity::cur] = cur.code
        data[PaymentDocumentEntity::expense] = Random.nextBoolean()
        data[PaymentDocumentEntity::account] = account.id
        data[PaymentDocumentEntity::paymentPurpose] = getRandomString(100)
        data[PaymentDocumentEntity::prop10] = getRandomString(10)
        data[PaymentDocumentEntity::prop15] = getRandomString(15)
        data[PaymentDocumentEntity::prop20] = getRandomString(20)
    }
}