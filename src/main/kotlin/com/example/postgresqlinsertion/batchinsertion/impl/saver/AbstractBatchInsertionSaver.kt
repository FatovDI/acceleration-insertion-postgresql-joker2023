package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionSaver
import java.sql.Connection
import javax.sql.DataSource

abstract class AbstractBatchInsertionSaver(
    dataSource: DataSource
): BatchInsertionSaver {

    val conn: Connection = dataSource.connection

    init {
        conn.autoCommit = false
    }

    override fun commit() {
        conn.commit()
    }

    override fun close() {
        conn.close()
    }
}