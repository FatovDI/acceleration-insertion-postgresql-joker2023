package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.io.DataOutputStream
import java.io.File
import java.nio.file.Paths
import java.util.*
import javax.sql.DataSource
import kotlin.reflect.KClass

open class CopyBinaryViaFileByEntitySaver<E: BaseEntity>(
    private val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    dataSource: DataSource,
    batchSize: Int
) : AbstractBatchInsertionByEntitySaver<E>(dataSource, batchSize) {

    private var file = File(Paths.get("./${UUID.randomUUID()}").toUri())
    private var writer = DataOutputStream(file.outputStream())

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
        processor.saveBinaryToDataBaseByCopyMethod(clazz = entityClass, from = file.inputStream(), conn = conn)
        file.delete()
        file = File(Paths.get("./${UUID.randomUUID()}").toUri())
        writer = DataOutputStream(file.outputStream())
        processor.startSaveBinaryDataForCopyMethod(writer)
    }

    override fun close() {
        writer.close()
        file.delete()
        super.close()
    }
}
