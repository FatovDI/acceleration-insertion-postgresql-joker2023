package com.example.postgresqlinsertion.batchinsertion.exception

class BatchInsertionException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable?) : super(message, cause)

}
