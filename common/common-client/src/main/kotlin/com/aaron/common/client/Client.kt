package com.aaron.common.client

import com.aaron.common.api.annotation.ServiceInterface
import com.aaron.common.client.circuit.DefaultRetryer
import com.aaron.common.client.circuit.NeverRetryer
import com.aaron.common.client.circuit.RefreshableTarget
import com.aaron.common.client.circuit.StableTarget
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.client.ClientException
import feign.Feign
import feign.Request
import feign.RetryableException
import feign.Retryer
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.jaxrs.JAXRSContract
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Created by Aaron Sheng on 2019/4/3.
 */
@Component
class Client @Autowired constructor(
    private val feignClient: feign.Client,
    private val clientErrorDecoder: ClientErrorDecoder,
    private val objectMapper: ObjectMapper
) {
    private val jaxRsContract = JAXRSContract()
    private val jacksonDecoder = JacksonDecoder(objectMapper)
    private val jacksonEncoder = JacksonEncoder(objectMapper)

    fun <T : Any> get(clz: KClass<T>): T {
        val stableTarget = StableTarget(clz.java)
        return Feign.builder()
            .client(feignClient)
            .errorDecoder(clientErrorDecoder)
            .encoder(jacksonEncoder)
            .decoder(jacksonDecoder)
            .contract(jaxRsContract)
            .options(Request.Options(5 * 1000, 15 * 1000))
            .retryer(NeverRetryer(stableTarget))
            .target(stableTarget)
    }

    fun <T : Any> getWithRetry(clz: KClass<T>): T {
        val refreshableTarget = RefreshableTarget(clz.java)
        return Feign.builder()
            .client(feignClient)
            .errorDecoder(clientErrorDecoder)
            .encoder(jacksonEncoder)
            .decoder(jacksonDecoder)
            .contract(jaxRsContract)
            .options(Request.Options(5 * 1000, 15 * 1000))
            .retryer(DefaultRetryer(refreshableTarget))
            .target(refreshableTarget)
    }
}
