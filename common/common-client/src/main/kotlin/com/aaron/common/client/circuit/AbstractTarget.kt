package com.aaron.common.client.circuit

import com.aaron.common.api.annotation.ServiceInterface
import com.aaron.common.api.exception.ClientException
import com.aaron.common.service.util.SpringContextUtil
import feign.Target
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.core.annotation.AnnotationUtils

/**
 * Created by Aaron Sheng on 2019/4/23.
 */
abstract class AbstractTarget<T> : Target<T> {

    protected fun getService(clz: Class<T>): String {
        val serviceName = getServiceName(clz)
        val serviceInstance = getServiceInstance(serviceName)

        return "${if (serviceInstance.isSecure) "https" else "http"}://${serviceInstance.host}:${serviceInstance.port}"
    }

    private fun getServiceInstance(serviceName: String): ServiceInstance {
        val discoveryClient = SpringContextUtil.getBean(DiscoveryClient::class.java)
        val instances = discoveryClient.getInstances(serviceName)
        if (instances == null || instances.isEmpty()) {
            throw ClientException("找不到任何有效的[$serviceName]服务提供者")
        }

        val circuit = SpringContextUtil.getBean(Circuit::class.java)
        val availableInstances = circuit.filterInstance(instances)
        if (availableInstances.isEmpty()) {
            throw ClientException("找不到任何可用的[$serviceName]服务提供者")
        }

        availableInstances.shuffle()
        return availableInstances[0]
    }

    private fun getServiceName(clz: Class<T>): String {
        val serviceInterface = AnnotationUtils.findAnnotation(clz, ServiceInterface::class.java)
        return if (serviceInterface != null) {
            serviceInterface.value
        } else {
            val packageName = clz.name
            val regex = Regex("com.aaron.([a-z]+).api.([a-zA-Z]+)")
            val matches = regex.find(packageName) ?: throw ClientException("无法根据接口[$packageName]分析所属的服务")
            matches.groupValues[1]
        }
    }
}