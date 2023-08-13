package com.example.postgresqlinsertion.batchinsertion.utils

fun String.toKebabCase() = stringToSplitCase(this, "-")

fun String.toSnakeCase() = stringToSplitCase(this, "_")

fun String.toSplitCase() = stringToSplitCase(this, " ")

fun stringToSplitCase(str: String, splitter: String): String {
    val replacement = "\$1$splitter\$2"
    return str.replace(Regex("([a-z])([A-Z]+)"), replacement).lowercase()
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}