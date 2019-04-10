package com.aaron.common.service

import com.aaron.common.service.util.SpringContextUtil
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.Ordered

/**
 * Created by Aaron Sheng on 2019/4/7.
 */
@Configuration
@PropertySource("classpath:/common-service.properties")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableDiscoveryClient
class ServiceAutoConfiguration {
    @Bean
    fun springContextUtil() = SpringContextUtil()
}