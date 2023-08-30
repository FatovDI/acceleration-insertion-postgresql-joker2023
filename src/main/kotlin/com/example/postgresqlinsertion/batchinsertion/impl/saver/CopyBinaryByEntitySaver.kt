package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import javax.sql.DataSource
import kotlin.reflect.KClass

open class CopyBinaryByEntitySaver<E : BaseEntity>(
    private val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    dataSource: DataSource,
) : AbstractBatchInsertionSaver(dataSource), BatchInsertionByEntitySaver<E> {

    private var byteArrayOs = ByteArrayOutputStream()
    private var writer = DataOutputStream(byteArrayOs)

    init {
        processor.startSaveBinaryDataForCopyMethod(writer)
    }

    override fun addDataForSave(entity: E) {
        ByteArrayOutputStream().toByteArray().inputStream()
        processor.addDataForCreateWithBinary(entity, writer)
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
        writer = DataOutputStream(byteArrayOs)
        processor.startSaveBinaryDataForCopyMethod(writer)
    }

    override fun close() {
        writer.close()
        super.close()
    }
}
