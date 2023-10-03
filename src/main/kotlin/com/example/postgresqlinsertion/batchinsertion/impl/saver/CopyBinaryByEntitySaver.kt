package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.sql.Connection
import kotlin.reflect.KClass

open class CopyBinaryByEntitySaver<E : BaseEntity>(
    private val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    conn: Connection,
    batchSize: Int
) : AbstractBatchInsertionByEntitySaver<E>(conn, batchSize) {

    private var byteArrayOs = ByteArrayOutputStream()
    private var writer = DataOutputStream(BufferedOutputStream(byteArrayOs))

    init {
        processor.startSaveBinaryDataForCopyMethod(writer)
    }

    override fun addDataForSave(entity: E) {
        processor.addDataForCreateWithBinary(entity, writer)
        super.addDataForSave(entity)
    }

    override fun saveData() {
        processor.endSaveBinaryDataForCopyMethod(writer)
        writer.close()
        processor.saveBinaryToDataBaseByCopyMethod(
            clazz = entityClass,
            from = byteArrayOs.toByteArray().inputStream(),
            conn = conn
        )
        byteArrayOs = ByteArrayOutputStream()
        writer = DataOutputStream(BufferedOutputStream(byteArrayOs))
        processor.startSaveBinaryDataForCopyMethod(writer)
    }

    override fun close() {
        writer.close()
        super.close()
    }
}
