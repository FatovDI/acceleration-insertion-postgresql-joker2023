package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.sql.Connection
import kotlin.reflect.KClass

open class UpdateByEntitySaver<E: BaseEntity>(
    private val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    conn: Connection,
    batchSize: Int
) : AbstractBatchInsertionByEntitySaver<E>(conn, batchSize) {

    private val dataForUpdate = mutableListOf<String>()

    override fun addDataForSave(entity: E) {
        dataForUpdate.add(processor.getStringForUpdate(entity))
        super.addDataForSave(entity)
    }

    override fun saveData() {
        processor.updateDataToDataBase(entityClass, dataForUpdate, conn)
        dataForUpdate.clear()
    }
}
