package com.example.postgresqlinsertion.service.batchinsertion.impl

import com.example.postgresqlinsertion.entity.BaseEntity
import com.example.postgresqlinsertion.service.batchinsertion.api.BatchInsertionByEntityProcessor
import java.io.File
import java.io.FileReader
import java.nio.file.Paths
import java.util.*
import javax.sql.DataSource
import kotlin.reflect.KClass

open class CopyModelViaFileSaver<E: BaseEntity>(
    private val processor: BatchInsertionByEntityProcessor,
    private val entityClass: KClass<E>,
    dataSource: DataSource,
) : AbstractBatchInsertionSaver<E>(dataSource) {

    private var file = File(Paths.get("./${UUID.randomUUID()}.csv").toUri())
    private var writer = file.bufferedWriter()

    override fun addDataForSave(entity: E) {
        processor.addDataForCreate(entity, writer)
    }

    override fun saveData() {
        writer.close()
        processor.saveToDataBaseByCopyMethod(entityClass, FileReader(file), conn)
        file.delete()
        file = File(Paths.get("./${UUID.randomUUID()}.csv").toUri())
        writer = file.bufferedWriter()
    }

    override fun close() {
        writer.close()
        file.delete()
        super.close()
    }
}
