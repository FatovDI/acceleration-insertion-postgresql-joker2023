package com.example.postgresqlinsertion.batchinsertion.impl.factory

import com.example.postgresqlinsertion.batchinsertion.api.factory.BatchInsertionByPropertyFactory
import com.example.postgresqlinsertion.batchinsertion.api.factory.SaverType
import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByPropertySaver
import com.example.postgresqlinsertion.batchinsertion.impl.saver.*
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import javax.sql.DataSource
import kotlin.reflect.KClass

abstract class BatchInsertionByPropertyFactory<E: BaseEntity>(
    private val entityClass: KClass<E>,
    override val processor: BatchInsertionByPropertyProcessor,
    private val dataSource: DataSource,
) : BatchInsertionByPropertyFactory<E> {

    override fun getSaver(type: SaverType): BatchInsertionByPropertySaver<E> {

        val conn = dataSource.connection

        return when (type) {
            SaverType.COPY -> CopyByPropertySaver(processor, entityClass, conn)
            SaverType.COPY_BINARY -> CopyBinaryByPropertySaver(processor, entityClass, conn)
            SaverType.COPY_VIA_FILE -> CopyViaFileByPropertySaver(processor, entityClass, conn)
            SaverType.COPY_BINARY_VIA_FILE -> CopyBinaryViaFileByPropertySaver(processor, entityClass, conn)
            SaverType.INSERT -> InsertByPropertySaver(processor, entityClass, conn)
            SaverType.UPDATE -> UpdateByPropertySaver(processor, entityClass, conn)
        }

    }
}
