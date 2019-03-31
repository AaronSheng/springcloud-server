package com.aaron.common.web

import io.swagger.jaxrs.config.BeanConfig
import io.swagger.jaxrs.listing.ApiListingResource
import io.swagger.jaxrs.listing.SwaggerSerializers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * Created by Aaron Sheng on 2018/6/12.
 */
@Component
class JerseySwaggerConfig : JerseyConfig() {
    @Value("\${spring.application.desc:#{null}}")
    private val applicationDesc: String? = null

    @Value("\${spring.application.version:#{null}}")
    private val applicationVersion: String? = null

    @Value("\${spring.application.packageName:#{null}}")
    private val packageName: String? = null

    @PostConstruct
    fun init() {
        configSwagger()
        register(SwaggerSerializers::class.java)
        register(ApiListingResource::class.java)
    }

    private fun configSwagger() {
        if (packageName != null && packageName.isNotBlank()) {
            BeanConfig().apply {
                title = applicationDesc
                version = applicationVersion
                resourcePackage = packageName
                scan = true
                basePath = "/api"
            }
        }
    }
}