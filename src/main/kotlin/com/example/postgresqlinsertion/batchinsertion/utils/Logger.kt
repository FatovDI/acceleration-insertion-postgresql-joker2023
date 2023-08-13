package com.example.postgresqlinsertion.batchinsertion.utils

import org.slf4j.LoggerFactory

fun <R : Any> R.logger() = lazy { LoggerFactory.getLogger(this::class.java.name) }
