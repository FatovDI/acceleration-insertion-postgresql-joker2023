package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByPropertySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import javax.sql.DataSource
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

open class CopyBinaryByPropertySaver<E: BaseEntity>(
    private val processor: BatchInsertionByPropertyProcessor,
    private val entityClass: KClass<E>,
    dataSource: DataSource,
) : AbstractBatchInsertionSaver(dataSource), BatchInsertionByPropertySaver<E> {

    private var byteArrayOs = ByteArrayOutputStream()
    private var writer = DataOutputStream(byteArrayOs)

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
            from = byteArrayOs.toByteArray().inputStream(),
            conn = conn
        )
        byteArrayOs = ByteArrayOutputStream()
        writer = DataOutputStream(byteArrayOs)
        processor.startSaveBinaryDataForCopyMethod(writer)
    }
}