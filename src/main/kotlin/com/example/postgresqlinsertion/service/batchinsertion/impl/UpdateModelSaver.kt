package com.example.postgresqlinsertion.service.batchinsertion.impl

import com.example.postgresqlinsertion.entity.BaseEntity
import com.example.postgresqlinsertion.service.batchinsertion.api.BatchInsertionByEntityProcessor
import javax.sql.DataSource
import kotlin.reflect.KClass

open class UpdateModelSaver<E: BaseEntity>(
    val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    dataSource: DataSource,
) : AbstractBatchInsertionSaver<E>(dataSource) {

    private val dataForUpdate = mutableListOf<String>()

    override fun addDataForSave(entity: E) {
        dataForUpdate.add(processor.getStringForUpdate(entity))
    }

    override fun saveData() {
        processor.updateDataToDataBase(entityClass, dataForUpdate, conn)
    }
}
