package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByPropertySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.io.StringWriter
import javax.sql.DataSource
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

open class CopyByPropertySaver<E: BaseEntity>(
    private val processor: BatchInsertionByPropertyProcessor,
    private val entityClass: KClass<E>,
    dataSource: DataSource,
) : AbstractBatchInsertionSaver(dataSource), BatchInsertionByPropertySaver<E> {

    private val delimiter = "|"
    private val nullValue = "NULL"
    private var writer = StringWriter()
    private var bufferedWriter = writer.buffered()

    override fun addDataForSave(data: Map<out KProperty1<E, *>, Any?>) {
        processor.addDataForCreate(data, bufferedWriter, delimiter, nullValue)
    }

    override fun saveData(columns: Set<KProperty1<E, *>>) {
        bufferedWriter.flush()
        processor.saveToDataBaseByCopyMethod(
            clazz = entityClass,
            columns = columns,
            delimiter = delimiter,
            nullValue = nullValue,
            from = writer.toString().reader(),
            conn = conn
        )
        writer = StringWriter()
        bufferedWriter = writer.buffered()
    }
}
