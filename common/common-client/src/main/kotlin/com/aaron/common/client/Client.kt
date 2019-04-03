package com.aaron.common.client

import com.aaron.common.api.pojo.annotation.ServiceInterface
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
    private val consulClient: ConsulDiscoveryClient,
    private val clientErrorDecoder: ClientErrorDecoder,
    private val objectMapper: ObjectMapper
) {
    private val interfaces = ConcurrentHashMap<KClass<*>, String>()
    private val jaxRsContract = JAXRSContract()
    private val jacksonDecoder = JacksonDecoder(objectMapper)
    private val jacksonEncoder = JacksonEncoder(objectMapper)

    fun <T : Any> get(clz: KClass<T>): T {
        val serviceName = findServiceName(clz)
        val serviceInstance = choose(serviceName)
        val url = "${if (serviceInstance.isSecure) "https" else "http"}://${serviceInstance.host}:${serviceInstance.port}/api"
        return Feign.builder()
            .client(feignClient)
            .errorDecoder(clientErrorDecoder)
            .encoder(jacksonEncoder)
            .decoder(jacksonDecoder)
            .contract(jaxRsContract)
            .target(clz.java, url)
    }

    fun <T : Any> getWithoutRetry(clz: KClass<T>): T {
        val serviceName = findServiceName(clz)
        val serviceInstance = choose(serviceName)
        val url = "${if (serviceInstance.isSecure) "https" else "http"}://${serviceInstance.host}:${serviceInstance.port}/api"
        return Feign.builder()
            .client(feignClient)
            .errorDecoder(clientErrorDecoder)
            .encoder(jacksonEncoder)
            .decoder(jacksonDecoder)
            .contract(jaxRsContract)
            .options(Request.Options(10 * 1000, 30 * 60 * 1000))
            .retryer(object : Retryer {
                override fun clone(): Retryer {
                    return this
                }

                override fun continueOrPropagate(e: RetryableException) {
                    throw e
                }
            })
            .target(clz.java, url)
    }

    private fun choose(serviceName: String): ServiceInstance {
        val instances = consulClient.getInstances(serviceName) ?: throw ClientException("找不到任何有效的[$serviceName]服务提供者")
        if (instances.isEmpty()) {
            throw ClientException("找不到任何有效的\"$serviceName\"服务提供者")
        }

        instances.shuffle()
        return instances[0]
    }

    private fun findServiceName(clz: KClass<*>): String {
        val findService = {
            val serviceInterface = AnnotationUtils.findAnnotation(clz.java, ServiceInterface::class.java)
            if (serviceInterface != null) {
                serviceInterface.value
            } else {
                val packageName = clz.qualifiedName.toString()
                val regex = Regex("com.aaron.([a-z]+).api.([a-zA-Z]+)")
                val matches = regex.find(packageName) ?: throw ClientException("无法根据接口\"$packageName\"分析所属的服务")
                matches.groupValues[1]
            }
        }
        return interfaces.getOrPut(clz, findService)
    }
}
