package com.aaron.common.api.pojo.exception

open class OperationException(message: String) : RuntimeException(message) {
    companion object {
        const val statusCode = 400
    }
}