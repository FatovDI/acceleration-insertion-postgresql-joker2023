package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.io.StringWriter
import javax.sql.DataSource
import kotlin.reflect.KClass

open class CopyByEntitySaver<E: BaseEntity>(
    private val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    dataSource: DataSource,
) : AbstractBatchInsertionSaver(dataSource), BatchInsertionByEntitySaver<E> {

    private var writer = StringWriter()
    private var bufferedWriter = writer.buffered()

    override fun addDataForSave(entity: E) {
        processor.addDataForCreate(entity, bufferedWriter)
    }

    override fun saveData() {
        bufferedWriter.flush()
        processor.saveToDataBaseByCopyMethod(entityClass, writer.toString().reader(), conn)
        writer = StringWriter()
        bufferedWriter = writer.buffered()
    }

}
