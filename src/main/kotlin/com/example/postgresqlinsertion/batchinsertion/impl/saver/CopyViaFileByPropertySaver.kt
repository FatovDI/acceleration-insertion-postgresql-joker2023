package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByPropertySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.io.File
import java.io.FileReader
import java.nio.file.Paths
import java.sql.Connection
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

open class CopyViaFileByPropertySaver<E : BaseEntity>(
    private val processor: BatchInsertionByPropertyProcessor,
    private val entityClass: KClass<E>,
    conn: Connection,
) : AbstractBatchInsertionSaver(conn), BatchInsertionByPropertySaver<E> {

    private val delimiter = "|"
    private val nullValue = "NULL"
    private var file = File(Paths.get("./${UUID.randomUUID()}.csv").toUri())
    private var writer = file.bufferedWriter()

    override fun addDataForSave(data: Map<out KProperty1<E, *>, Any?>) {
        processor.addDataForCreate(data, writer, delimiter, nullValue)
    }

    override fun saveData(columns: Set<KProperty1<E, *>>) {
        writer.close()
        processor.saveToDataBaseByCopyMethod(
            clazz = entityClass,
            columns = columns,
            delimiter = delimiter,
            nullValue = nullValue,
            from = FileReader(file),
            conn = conn
        )
        file.delete()
        file = File(Paths.get("./${UUID.randomUUID()}.csv").toUri())
        writer = file.bufferedWriter()
    }

    override fun close() {
        writer.close()
        file.delete()
        super.close()
    }
}
