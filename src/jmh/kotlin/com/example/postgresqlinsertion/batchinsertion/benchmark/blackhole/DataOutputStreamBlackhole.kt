package com.example.postgresqlinsertion.batchinsertion.benchmark.blackhole

import org.openjdk.jmh.infra.Blackhole
import java.io.DataOutputStream
import java.io.FileOutputStream

class DataOutputStreamBlackhole(
    private val bh: Blackhole,
    private val fos: FileOutputStream
): DataOutputStream(fos) {
    override fun write(b: Int) {
        bh.consume(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        bh.consume(b)
    }

    override fun write(b: ByteArray) {
        bh.consume(b)
    }
}