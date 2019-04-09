package com.aaron.common.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

/**
 * Created by Aaron Sheng on 2019/4/9.
 */
object JsonUtil {
    private val objectMapper = ObjectMapper().apply {
        registerModule(KotlinModule())
        configure(SerializationFeature.INDENT_OUTPUT, true)
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
        configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    }

    private val skipEmptyObjectMapper = ObjectMapper().apply {
        registerModule(KotlinModule())
        configure(SerializationFeature.INDENT_OUTPUT, true)
        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
        configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
        setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    }

    fun getObjectMapper() = objectMapper
}