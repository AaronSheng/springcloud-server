package com.aaron.common.client

import com.aaron.common.client.circuit.Circuit
import com.fasterxml.jackson.databind.ObjectMapper
import feign.okhttp.OkHttpClient
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.Ordered
import java.util.concurrent.TimeUnit

/**
 * Created by Aaron Sheng on 2019/4/18.
 */
@Configuration
@PropertySource("classpath:/common-client.properties")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureAfter(LoadBalancerAutoConfiguration::class)
class ClientAutoConfiguration {

    @Bean
    fun circuit() = Circuit()

    @Bean
    fun okHttpClient() = OkHttpClient(
        okhttp3.OkHttpClient.Builder()
            .connectTimeout(5L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
            .build()
    )

    @Bean
    fun clientErrorDecoder(objectMapper: ObjectMapper) = ClientErrorDecoder(objectMapper)

    @Bean
    @ConditionalOnMissingBean(Client::class)
    fun client(
        clientErrorDecoder: ClientErrorDecoder,
        objectMapper: ObjectMapper,
        okHttpClient: feign.Client
    ) = Client(okHttpClient, clientErrorDecoder, objectMapper)
}