package com.example.postgresqlinsertion.batchinsertion.api

import com.example.postgresqlinsertion.logic.entity.BaseEntity
import kotlin.reflect.KClass

interface SqlHelper {

    /**
     * Get list id by count
     * @param count - count of id
     * @return List<Long> - list of id
     */
    fun nextIdList(count: Int): List<Long>

    /**
     * Drop index by entity
     * @param clazz - entity class
     * @return String - string of script to create dropped index
     */
    fun dropIndex(clazz: KClass<out BaseEntity>): String

    /**
     * Execute script
     * @param script - script for execute
     */
    fun executeScript(script: String)
}