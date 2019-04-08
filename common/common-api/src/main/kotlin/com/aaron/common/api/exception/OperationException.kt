package com.aaron.common.api.exception

open class OperationException(message: String) : RuntimeException(message) {
    companion object {
        const val statusCode = 400
    }
}