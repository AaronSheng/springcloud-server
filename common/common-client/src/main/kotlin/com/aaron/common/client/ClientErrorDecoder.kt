package com.aaron.common.client

import com.aaron.common.api.pojo.Result
import com.aaron.common.api.pojo.exception.ClientException
import com.aaron.common.api.pojo.exception.OperationException
import com.aaron.common.api.pojo.exception.RemoteServiceException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException
import java.lang.Exception

@Service
class ClientErrorDecoder @Autowired constructor(val objectMapper: ObjectMapper) : ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
        // 首先判断返回结果是否能被序列化
        val responseStream = response.body().asInputStream()
        val result: Result<*>
        try {
            result = objectMapper.readValue(responseStream)
        } catch (e: IOException) {
            return ClientException("内部服务返回结果无法解析")
        }
        if (response.status() == OperationException.statusCode) {
            throw OperationException(result.message ?: "")
        }
        return RemoteServiceException(result.message ?: "", response.status())
    }
}