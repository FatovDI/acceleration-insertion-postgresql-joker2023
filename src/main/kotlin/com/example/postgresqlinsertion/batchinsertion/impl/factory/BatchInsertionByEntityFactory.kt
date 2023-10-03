package com.example.postgresqlinsertion.batchinsertion.impl.factory

import com.example.postgresqlinsertion.batchinsertion.api.factory.BatchInsertionByEntityFactory
import com.example.postgresqlinsertion.batchinsertion.api.factory.SaverType
import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.batchinsertion.impl.saver.*
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import javax.sql.DataSource
import kotlin.reflect.KClass

abstract class BatchInsertionByEntityFactory<E: BaseEntity>(
    private val entityClass: KClass<E>,
) : BatchInsertionByEntityFactory<E> {

    @Value("\${batch_insertion.batch_size}")
    lateinit var batchSize: String

    @Autowired
    override lateinit var processor: BatchInsertionByEntityProcessor

    @Autowired
    override lateinit var dataSource: DataSource

    override fun getSaver(type: SaverType): BatchInsertionByEntitySaver<E> {

        val batchSizeInt = batchSize.toInt()
        val conn = dataSource.connection

        return when (type) {
            SaverType.COPY -> CopyByEntitySaver(processor, entityClass, conn, batchSizeInt)
            SaverType.COPY_BINARY -> CopyBinaryByEntitySaver(processor, entityClass, conn, batchSizeInt)
            SaverType.COPY_VIA_FILE -> CopyViaFileByEntitySaver(processor, entityClass, conn, batchSizeInt)
            SaverType.COPY_BINARY_VIA_FILE -> CopyBinaryViaFileByEntitySaver(processor, entityClass, conn, batchSizeInt)
            SaverType.INSERT -> InsertByEntitySaver(processor, entityClass, conn, batchSizeInt)
            SaverType.UPDATE -> UpdateByEntitySaver(processor, entityClass, conn, batchSizeInt)
        }

    }
}
