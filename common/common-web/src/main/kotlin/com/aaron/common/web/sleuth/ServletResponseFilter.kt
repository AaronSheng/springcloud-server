package com.aaron.common.web.sleuth

import brave.Tracer
import com.aaron.common.service.util.SpringContextUtil
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Aaron Sheng on 2019/4/7.
 */
@Component
class ServletResponseFilter : OncePerRequestFilter() {
    private val REQUEST_ID = "Request-Id"

    override fun doFilterInternal(httpServletRequest: HttpServletRequest?, httpServletResponse: HttpServletResponse?, filterChain: FilterChain?) {
        val tracer = SpringContextUtil.getBean(Tracer::class.java)

        httpServletResponse?.addHeader(REQUEST_ID, tracer.currentSpan().context().traceIdString())
        filterChain?.doFilter(httpServletRequest, httpServletResponse)
    }
}