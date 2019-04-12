package com.aaron.common.web.sleuth

import java.io.ByteArrayOutputStream
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

/**
 * Created by Aaron Sheng on 2019/4/11.
 */
class CacheServletOutputStream: ServletOutputStream() {
    private val cache = ByteArrayOutputStream(1024)

    override fun setWriteListener(p0: WriteListener?) {
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun write(b: Int) {
        cache.write(b)
    }

    override fun write(b: ByteArray) {
        cache.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        cache.write(b, off, len)
    }

    fun getByteArray(): ByteArray {
        return cache.toByteArray()
    }
}