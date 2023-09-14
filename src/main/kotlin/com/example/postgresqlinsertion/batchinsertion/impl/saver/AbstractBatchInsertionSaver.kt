package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionSaver
import com.example.postgresqlinsertion.batchinsertion.utils.logger
import java.sql.Connection
import java.time.LocalDateTime
import javax.sql.DataSource

abstract class AbstractBatchInsertionSaver(
    dataSource: DataSource
): BatchInsertionSaver {

    val conn: Connection = dataSource.connection

    val log by logger()

    init {
        log.info("start save data by ${this.javaClass.simpleName} at ${LocalDateTime.now()}")
        conn.autoCommit = false
    }

    override fun commit() {
        conn.commit()
    }

    override fun close() {
        conn.close()
        log.info("end save data by ${this.javaClass.simpleName} at ${LocalDateTime.now()}")
    }
}