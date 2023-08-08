package com.example.postgresqlinsertion.service.batchinsertion.impl

import com.example.postgresqlinsertion.entity.BaseEntity
import com.example.postgresqlinsertion.service.batchinsertion.api.BatchInsertionByEntityProcessor
import javax.sql.DataSource
import kotlin.reflect.KClass

open class InsertModelSaver<E: BaseEntity>(
    private val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    dataSource: DataSource,
) : AbstractBatchInsertionSaver<E>(dataSource) {

    private val dataForInsert = mutableListOf<String>()

    override fun addDataForSave(entity: E) {
        dataForInsert.add(processor.getStringForInsert(entity))
    }

    override fun saveData() {
        processor.insertDataToDataBase(entityClass, dataForInsert, conn)
        dataForInsert.clear()
    }

    override fun commit() {
        conn.commit()
    }
}
