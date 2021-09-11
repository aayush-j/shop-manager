package com.rttc.shopmanager.utilities

import java.io.InputStream
import java.io.OutputStream

object DatabaseHelper {
    fun copyFileStreams(inputStream: InputStream, outputStream: OutputStream): Boolean {
        return try {
            inputStream.use { inp ->
                outputStream.use { out ->
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inp.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}