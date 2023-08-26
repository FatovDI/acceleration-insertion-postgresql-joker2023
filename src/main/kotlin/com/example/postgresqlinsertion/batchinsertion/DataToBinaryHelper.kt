package com.example.postgresqlinsertion.batchinsertion

import org.postgresql.util.ByteConverter
import java.io.DataOutputStream
import java.math.BigDecimal
import java.sql.Date
import java.time.LocalDate
import java.util.*

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
    var millis = sqlData.time
    millis += tz.getOffset(millis).toLong()
    val secs = toPgSecs(millis / 1000)
    ByteConverter.int4(buf, 0, (secs / 86400).toInt())

    outputStream.write(buf)
}

fun writeBigDecimal(data: BigDecimal, outputStream: DataOutputStream) {

    val bytes = ByteConverter.numeric(data)

    outputStream.writeInt(bytes.size)
    outputStream.write(bytes)

}

/**
 * from org.postgresql.jdbc.TimestampUtils
 *
 * Converts the given java seconds to postgresql seconds. See {@link #toJavaSecs} for the reverse
 * operation. The conversion is valid for any year 100 BC onwards.
 *
 * @param seconds Postgresql seconds.
 * @return Java seconds.
 */
private fun toPgSecs(seconds: Long): Long {
    var secs = seconds
    // java epoc to postgres epoc
    secs -= 946684800L

    // Julian/Gregorian calendar cutoff point
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

