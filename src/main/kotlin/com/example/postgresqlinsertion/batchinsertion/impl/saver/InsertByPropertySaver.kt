package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByPropertySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

open class InsertByPropertySaver<E: BaseEntity>(
    private val processor: BatchInsertionByPropertyProcessor,
    private val entityClass: KClass<E>,
    conn: Connection,
) : AbstractBatchInsertionSaver(conn), BatchInsertionByPropertySaver<E> {

    private val nullValue = "NULL"
    private val dataForInsert = mutableListOf<String>()

    override fun addDataForSave(data: Map<out KProperty1<E, *>, Any?>) {
        dataForInsert.add(processor.getStringForInsert(data, nullValue))
    }

    override fun saveData(columns: Set<KProperty1<E, *>>) {
        processor.insertDataToDataBase(entityClass, columns, dataForInsert, conn)
        dataForInsert.clear()
    }
}
