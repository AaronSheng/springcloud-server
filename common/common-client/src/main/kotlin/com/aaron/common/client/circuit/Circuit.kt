package com.aaron.common.client.circuit

import com.aaron.common.util.over
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.ServiceInstance
import org.springframework.stereotype.Component
import java.net.URI
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Aaron Sheng on 2019/4/23.
 */
@Component
class Circuit {
    @Value("\${circuit.retry.period:#{null}}")
    private val period: Long? = 30

    private val circuits = ConcurrentHashMap<String, LocalDateTime>()

    fun filterInstance(instances: List<ServiceInstance>): MutableList<ServiceInstance> {
        return instances.filter { instance ->
            val key = compositeKey(instance)
            val localDateTime = circuits[key]
            if (localDateTime != null) {
                if (localDateTime.over()) {
                    logger.info("circuit remove key($key)")
                    circuits.remove(key)
                }
                localDateTime.over()
            } else {
                true
            }
        }.toMutableList()
    }

    fun addCircuitInstance(url: String, seconds: Long? = null) {
        val retryPeriod = seconds ?: period!!
        val key = compositeKey(url)

        logger.info("circuit add key($key) period($retryPeriod)")
        circuits[key] = LocalDateTime.now().plusSeconds(retryPeriod)
    }

    fun compositeKey(url: String): String {
        val uri = URI.create(url)
        return "${uri.host}:${uri.port}"
    }

    fun compositeKey(serviceInstance: ServiceInstance): String {
        return "${serviceInstance.host}:${serviceInstance.port}"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}