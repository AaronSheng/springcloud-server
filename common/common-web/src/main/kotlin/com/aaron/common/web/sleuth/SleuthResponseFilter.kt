package com.aaron.common.web.sleuth

import brave.Tracer
import com.aaron.common.service.util.SpringContextUtil
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

    override fun filter(requestContext: ContainerRequestContext?, responseContext: ContainerResponseContext?) {
        val headers = responseContext?.headers
        if (headers != null && !headers.containsKey(REQUEST_ID)) {
            val tracer = SpringContextUtil.getBean(Tracer::class.java)
            headers.add(REQUEST_ID, tracer.currentSpan().context().traceIdString())
        }
    }
}