package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByPropertySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.nio.file.Paths
import java.sql.Connection
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

open class CopyBinaryViaFileByPropertySaver<E : BaseEntity>(
    private val processor: BatchInsertionByPropertyProcessor,
    private val entityClass: KClass<E>,
    conn: Connection,
) : AbstractBatchInsertionSaver(conn), BatchInsertionByPropertySaver<E> {

    private var file = File(Paths.get("./${UUID.randomUUID()}").toUri())
    private var writer = DataOutputStream(BufferedOutputStream(file.outputStream()))

    init {
        processor.startSaveBinaryDataForCopyMethod(writer)
    }

    override fun addDataForSave(data: Map<out KProperty1<E, *>, Any?>) {
        processor.addDataForCreateWithBinary(data, writer)
    }

    override fun saveData(columns: Set<KProperty1<E, *>>) {
        processor.endSaveBinaryDataForCopyMethod(writer)
        processor.saveBinaryToDataBaseByCopyMethod(
            clazz = entityClass,
            columns = columns,
            from = file.inputStream(),
            conn = conn
        )
        file.delete()
        file = File(Paths.get("./${UUID.randomUUID()}").toUri())
        writer = DataOutputStream(BufferedOutputStream(file.outputStream()))
        processor.startSaveBinaryDataForCopyMethod(writer)
    }

    override fun close() {
        writer.close()
        file.delete()
        super.close()
    }
}
