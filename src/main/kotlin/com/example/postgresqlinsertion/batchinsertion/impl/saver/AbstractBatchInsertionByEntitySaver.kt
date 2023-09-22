package com.example.postgresqlinsertion.batchinsertion.impl.saver

import com.example.postgresqlinsertion.batchinsertion.api.saver.BatchInsertionByEntitySaver
import com.example.postgresqlinsertion.logic.entity.BaseEntity
import java.time.LocalDateTime
import javax.sql.DataSource

abstract class AbstractBatchInsertionByEntitySaver<E : BaseEntity>(
    dataSource: DataSource,
    private val batchSize: Int
) : AbstractBatchInsertionSaver(dataSource), BatchInsertionByEntitySaver<E> {

    private var counter = 0

    override fun addDataForSave(entity: E) {
        counter++
        if (counter % batchSize == 0) {
            log.info("save batch insertion $batchSize by ${this.javaClass.simpleName} at ${LocalDateTime.now()}")
            saveData()
        }
    }

    override fun commit() {
        if (counter % batchSize != 0) {
            saveData()
        }
        log.info("start commit $counter data by ${this.javaClass.simpleName} at ${LocalDateTime.now()}")
        counter = 0
        super.commit()
    }

}