package com.example.postgresqlinsertion.batchinsertion.benchmark.blackhole

import org.openjdk.jmh.infra.Blackhole
import java.io.DataOutputStream
import java.io.OutputStream

class DataOutputStreamBlackhole(
    private val bh: Blackhole,
    os: OutputStream
): DataOutputStream(os) {
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