package com.aaron.common.web.sleuth

import java.io.OutputStreamWriter
import java.io.PrintWriter
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper


/**
 * Created by Aaron Sheng on 2019/4/11.
 */
class CacheHttpServletResponse constructor(response: HttpServletResponse) : HttpServletResponseWrapper(response) {
    private var outputStream: ServletOutputStream? = null
    private var writer: PrintWriter? = null
    private var cacheServletOutputStream = CacheServletOutputStream()

    override fun getOutputStream(): ServletOutputStream {
        return cacheServletOutputStream
    }

    override fun getWriter(): PrintWriter {
        if (outputStream != null) {
            throw IllegalStateException("getOutputStream() has already been called on this response.")
        }

        if (writer == null) {
            writer = PrintWriter(OutputStreamWriter(cacheServletOutputStream, response.characterEncoding), true)
        }

        return writer!!
    }

    fun getContent(): ByteArray {
        return cacheServletOutputStream.getByteArray()
    }
}