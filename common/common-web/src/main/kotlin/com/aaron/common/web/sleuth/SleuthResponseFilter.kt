package com.aaron.common.web.sleuth

import brave.Tracer
import com.aaron.common.api.pojo.Result
import com.aaron.common.service.util.SpringContextUtil
import com.aaron.common.util.JsonUtil
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.ext.Provider

/**
 * Created by Aaron Sheng on 2019/4/7.
 */
@Provider
class SleuthResponseFilter : ContainerResponseFilter {
    private val REQUEST_ID = "Request-Id"
    private val objectMapper = JsonUtil.getObjectMapper()

    override fun filter(requestContext: ContainerRequestContext?, responseContext: ContainerResponseContext?) {
        val tracer = SpringContextUtil.getBean(Tracer::class.java)

        val headers = responseContext?.headers
        if (headers != null && !headers.containsKey(REQUEST_ID)) {
            headers.add(REQUEST_ID, tracer.currentSpan().context().traceIdString())
        }

        val entity = responseContext?.entity
        if (entity != null && entity is Result<*>) {
            tracer.currentSpan().tag("result", objectMapper.writeValueAsString(entity))
        }
    }
}