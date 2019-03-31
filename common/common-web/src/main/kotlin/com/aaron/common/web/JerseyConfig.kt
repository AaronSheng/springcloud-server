package com.aaron.common.web

import org.glassfish.jersey.server.ResourceConfig
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import javax.ws.rs.ApplicationPath

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
@ApplicationPath("/api")
open class JerseyConfig : ResourceConfig(), ApplicationContextAware, InitializingBean {
    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun afterPropertiesSet() {
        val restResources = applicationContext.getBeansWithAnnotation(RestResource::class.java)
        restResources.values.forEach {
            register(it)
        }
    }
}