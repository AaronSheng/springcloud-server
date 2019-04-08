package com.aaron.common.api.exception

class RemoteServiceException(val errorMessage: String, val httpStatus: Int = 500) : RuntimeException(errorMessage)