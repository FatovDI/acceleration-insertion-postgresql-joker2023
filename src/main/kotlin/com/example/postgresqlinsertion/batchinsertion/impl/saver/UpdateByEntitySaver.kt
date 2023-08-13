package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import javax.sql.DataSource
import kotlin.reflect.KClass

open class UpdateByEntitySaver<E: BaseEntity>(
    private val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    dataSource: DataSource,
) : AbstractBatchInsertionSaver(dataSource), BatchInsertionByEntitySaver<E> {

    private val dataForUpdate = mutableListOf<String>()

    override fun addDataForSave(entity: E) {
        dataForUpdate.add(processor.getStringForUpdate(entity))
    }

    override fun saveData() {
        processor.updateDataToDataBase(entityClass, dataForUpdate, conn)
    }
}
