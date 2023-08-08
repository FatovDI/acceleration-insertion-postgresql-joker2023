package com.example.postgresqlinsertion.service.batchinsertion.impl

import com.example.postgresqlinsertion.service.batchinsertion.api.BatchInsertionSaver
import java.sql.Connection
import javax.sql.DataSource

abstract class AbstractBatchInsertionSaver<E>(
    dataSource: DataSource
): BatchInsertionSaver<E> {

    val conn: Connection = dataSource.connection

    init {
        conn.autoCommit = false
    }

    // todo подумать, можно ли переделать красиво обертку с транзакциями. Смотри AuditingG2EntityListener
    override fun commit() {
        conn.commit()
    }

    override fun close() {
        conn.close()
    }
}