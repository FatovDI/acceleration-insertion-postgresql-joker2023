package com.example.postgresqlinsertion.batchinsertion

import org.postgresql.util.ByteConverter
import java.io.DataOutputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Date
import java.time.LocalDate
import java.util.*
import kotlin.math.pow

// todo переделать writeBigDecimal. Подумать?
fun writeLong(data: Long, outputStream: DataOutputStream){
    outputStream.writeInt(8)
    outputStream.writeLong(data)
}

fun writeBoolean(data: Boolean, outputStream: DataOutputStream){
    outputStream.writeInt(1)
    outputStream.writeByte(if (data) 1 else 0)
}

fun writeString(data: String, outputStream: DataOutputStream){
    val bytes = data.toByteArray()

    outputStream.writeInt(bytes.size)
    outputStream.write(bytes)
}

fun writeLocalDate(data: LocalDate, outputStream: DataOutputStream){

    outputStream.writeInt(4)

    val sqlData = Date.valueOf(data)

    val buf = ByteArray(4)
    val tz = TimeZone.getDefault()
    var millis = sqlData.getTime()
    millis += tz.getOffset(millis).toLong()
    val secs = toPgSecs(millis / 1000)
    ByteConverter.int4(buf, 0, (secs / 86400).toInt())

    outputStream.write(buf)
}

fun writeBigDecimal(data: BigDecimal, outputStream: DataOutputStream) {

    // Number of fractional digits:
    val fractionDigits = data.scale()

    // Number of Fraction Groups:
    val fractionGroups = if (fractionDigits > 0) (fractionDigits + 3) / 4 else 0

    val digits = digits(data)

    outputStream.writeInt(8 + 2 * digits.size)
    outputStream.writeShort(digits.size)
    outputStream.writeShort(digits.size - fractionGroups - 1)
    outputStream.writeShort(if (data.signum() == 1) 0x0000 else 0x4000)
    outputStream.writeShort(if (fractionDigits > 0) fractionDigits else 0)

    // Now write each digit:
    for (pos in digits.indices.reversed()) {
        val valueToWrite = digits[pos]
        outputStream.writeShort(valueToWrite)
    }
}

private fun digits(value: BigDecimal): List<Int> {
    var unscaledValue = value.unscaledValue()
    if (value.signum() == -1) {
        unscaledValue = unscaledValue.negate()
    }
    val digits = mutableListOf<Int>()
    if (value.scale() > 0) {
        // The scale needs to be a multiple of 4:
        val scaleRemainder = value.scale() % 4

        // Scale the first value:
        if (scaleRemainder != 0) {
            val result = unscaledValue.divideAndRemainder(BigInteger.TEN.pow(scaleRemainder))
            val digit =
                result[1].toInt() * 10.0.pow((4 - scaleRemainder).toDouble())
                    .toInt()
            digits.add(digit)
            unscaledValue = result[0]
        }
        while (unscaledValue != BigInteger.ZERO) {
            val result = unscaledValue.divideAndRemainder(BigInteger("10000"))
            digits.add(result[1].toInt())
            unscaledValue = result[0]
        }
    } else {
        var originalValue = unscaledValue.multiply(BigInteger.TEN.pow(Math.abs(value.scale())))
        while (originalValue != BigInteger.ZERO) {
            val result = originalValue.divideAndRemainder(BigInteger("10000"))
            digits.add(result[1].toInt())
            originalValue = result[0]
        }
    }
    return digits
}

/**
 * Converts the given java seconds to postgresql seconds. The conversion is valid for any year 100 BC onwards.
 *
 * from /org/postgresql/jdbc2/TimestampUtils.java
 *
 * @param seconds Postgresql seconds.
 * @return Java seconds.
 */
private fun toPgSecs(seconds: Long): Long {
    var secs = seconds
    // java epoc to postgres epoc
    secs -= 946684800L

    // Julian/Greagorian calendar cutoff point
    if (secs < -13165977600L) { // October 15, 1582 -> October 4, 1582
        secs -= 86400 * 10
        if (secs < -15773356800L) { // 1500-03-01 -> 1500-02-28
            var years = ((secs + 15773356800L) / -3155823050L).toInt()
            years++
            years -= years / 4
            secs += years * 86400.toLong()
        }
    }
    return secs
}

