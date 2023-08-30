package com.example.postgresqlinsertion.batchinsertion.impl.processor

import com.example.postgresqlinsertion.batchinsertion.api.processor.BatchInsertionByPropertyProcessor
import com.example.postgresqlinsertion.batchinsertion.getColumnsString
import com.example.postgresqlinsertion.batchinsertion.getTableName
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import org.springframework.stereotype.Component
import java.io.BufferedWriter
import java.io.DataOutputStream
import java.io.InputStream
import java.io.Reader
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

@Component
class PostgresBatchInsertionByPropertyProcessor(
) : AbstractBatchInsertionProcessor(), BatchInsertionByPropertyProcessor {

    override fun addDataForCreate(
        data: Map<out KProperty1<out BaseEntity, *>, Any?>,
        writer: BufferedWriter,
        delimiter: String,
        nullValue: String
    ) {
        writer.write(getStringForWrite(data.values.map { it?.toString() }, delimiter, nullValue))
        writer.newLine()
    }

    override fun addDataForCreateWithBinary(
        data: Map<out KProperty1<out BaseEntity, *>, Any?>,
        outputStream: DataOutputStream
    ) {
        outputStream.writeShort(data.size)
        data.map { field ->

            writeBinaryDataForCopyMethod(field.value, outputStream)

        }
    }

    override fun getStringForUpdate(
        data: Map<out KProperty1<out BaseEntity, *>, Any?>,
        id: Long,
        nullValue: String
    ) = getStringForInsert(data, nullValue).let { "($it) where id = '$id'" }

    override fun saveToDataBaseByCopyMethod(
        clazz: KClass<out BaseEntity>,
        columns: Set<KProperty1<out BaseEntity, *>>,
        delimiter: String,
        nullValue: String,
        from: Reader,
        conn: Connection
    ) {
        saveToDataBaseByCopyMethod(getTableName(clazz), getColumnsString(columns), delimiter, nullValue, from, conn)
    }

    override fun saveBinaryToDataBaseByCopyMethod(
        clazz: KClass<out BaseEntity>,
        columns: Set<KProperty1<out BaseEntity, *>>,
        from: InputStream,
        conn: Connection
    ) {
        saveBinaryToDataBaseByCopyMethod(getTableName(clazz), getColumnsString(columns), from, conn)
    }

    override fun insertDataToDataBase(
        clazz: KClass<out BaseEntity>,
        columns: Set<KProperty1<out BaseEntity, *>>,
        data: List<String>,
        conn: Connection
    ) {
        insertDataToDataBase(getTableName(clazz), getColumnsString(columns), data, conn)
    }

    override fun updateDataToDataBase(
        clazz: KClass<out BaseEntity>,
        columns: Set<KProperty1<out BaseEntity, *>>,
        data: List<String>,
        conn: Connection
    ) {
        updateDataToDataBase(getTableName(clazz), getColumnsString(columns), data, conn)
    }

    override fun getStringForInsert(data: Map<out KProperty1<out BaseEntity, *>, Any?>, nullValue: String) =
        getStringForInsert(data.values.map { it?.toString() }, nullValue)

}
