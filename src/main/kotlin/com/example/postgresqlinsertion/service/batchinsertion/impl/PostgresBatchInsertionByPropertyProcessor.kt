package com.example.postgresqlinsertion.service.batchinsertion.impl

import com.example.postgresqlinsertion.entity.BaseEntity
import com.example.postgresqlinsertion.service.batchinsertion.api.IBatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.service.batchinsertion.getColumnsString
import com.example.postgresqlinsertion.service.batchinsertion.getTableName
import org.springframework.stereotype.Component
import java.io.BufferedWriter
import java.io.Reader
import javax.sql.DataSource
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

@Component
class PostgresBatchInsertionByPropertyProcessor(
    val dataSource: DataSource,
) : AbstractBatchInsertionProcessor(), IBatchInsertionByPropertyProcessor { // todo move tests

    private val delimiter = "|"
    private val nullValue = "NULL"

    override fun addDataForCreate(data: Map<out KProperty1<out BaseEntity, *>, String?>, writer: BufferedWriter) {
        writer.write(getStringForWrite(data.values, delimiter, nullValue))
        writer.newLine()
    }

    override fun getStringForUpdate(data: Map<out KProperty1<out BaseEntity, *>, String?>, id: Long) =
        getStringForInsert(data).let { "($it) where id = '$id'" }

    override fun saveToDataBaseByCopyMethod(
        clazz: KClass<out BaseEntity>,
        columns: Set<out KProperty1<out BaseEntity, *>>,
        from: Reader
    ) {
        dataSource.connection.use { conn ->
            saveToDataBaseByCopyMethod(getTableName(clazz), getColumnsString(columns), delimiter, nullValue, from, conn)
        }
    }

    override fun insertDataToDataBase(
        clazz: KClass<out BaseEntity>,
        columns: Set<out KProperty1<out BaseEntity, *>>,
        data: List<String>
    ) {
        dataSource.connection.use { conn ->
            insertDataToDataBase(getTableName(clazz), getColumnsString(columns), data, conn)
        }
    }

    override fun updateDataToDataBase(
        clazz: KClass<out BaseEntity>,
        columns: Set<out KProperty1<out BaseEntity, *>>,
        data: List<String>
    ) {
        dataSource.connection.use { conn ->
            updateDataToDataBase(getTableName(clazz), getColumnsString(columns), data, conn)
        }
    }

    override fun getStringForInsert(data: Map<out KProperty1<out BaseEntity, *>, String?>) =
        getStringForInsert(data.values, nullValue)

}
