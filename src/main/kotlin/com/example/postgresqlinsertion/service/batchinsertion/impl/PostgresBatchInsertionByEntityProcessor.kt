package com.example.postgresqlinsertion.service.batchinsertion.impl

import com.example.postgresqlinsertion.entity.BaseEntity
import com.example.postgresqlinsertion.service.batchinsertion.api.BatchInsertionByEntityProcessor
import com.example.postgresqlinsertion.service.batchinsertion.getDataFromEntity
import org.springframework.stereotype.Component
import java.io.BufferedWriter
import java.io.Reader
import java.sql.Connection
import kotlin.reflect.KClass

@Component
class PostgresBatchInsertionByEntityProcessor(
): AbstractBatchInsertionProcessor(), BatchInsertionByEntityProcessor {

    private val delimiter = "|"
    private val nullValue = "NULL"

    override fun addDataForCreate(data: BaseEntity, writer: BufferedWriter) {
        val values = getDataFromEntity(data)
        writer.write(getStringForWrite(values, delimiter, nullValue))
        writer.newLine()
    }
    override fun getStringForUpdate(data: BaseEntity) =
        getStringForInsert(data).let { "($it) where id = '${data.id}'" }

    override fun getStringForInsert(data: BaseEntity) =
        getStringForInsert(getDataFromEntity(data), nullValue)

    override fun saveToDataBaseByCopyMethod(clazz: KClass<out BaseEntity>, from: Reader, conn: Connection) {
        saveToDataBaseByCopyMethod(clazz, from, delimiter, nullValue, conn)
    }

}
