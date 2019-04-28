package com.aaron.common.client.circuit

import com.aaron.common.util.difference
import com.aaron.common.util.overtime
import com.aaron.common.util.timestamp
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.ServiceInstance
import org.springframework.stereotype.Component
import java.net.URI
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import javax.annotation.PostConstruct

/**
 * Created by Aaron Sheng on 2019/4/23.
 */
@Component
class Circuit {
    @Value("\${circuit.breaker.sleepWindowInSeconds:#{null}}")
    private val sleepWindowInSeconds: Long? = 30
    @Value("\${circuit.breaker.requestVolumeThreshold:#{null}}")
    private val requestVolumeThreshold: Long? = 10

    // fail request count window
    private val defaultRequestWindow = 60

    // fail request count
    private val failRequests = ConcurrentHashMap<String, FailRequest>()

    // broken service
    private val breakers = ConcurrentHashMap<String, LocalDateTime>()

    // breaker lock to protect concurrent resource
    private val locks = ConcurrentHashMap<String, ReentrantLock>()

    private val timers = Timer()

    @PostConstruct
    fun startTimer() {
        timers.schedule(object : TimerTask() {
            override fun run() {
                clearOvertimeBreaker()
            }
        }, 15000, 5000)
    }

    private fun clearOvertimeBreaker() {
        val breakerSize = breakers.size

        for (index in 1..breakerSize) {
            run breaking@{
                breakers.forEach { breaker ->
                    if (breaker.value.overtime()) {
                        val lock = getLock(breaker.key)
                        try {
                            lock.lock()
                            breakers.remove(breaker.key)
                            logger.info("circuit remove key(${breaker.key})")
                        } finally {
                            lock.unlock()
                        }
                        return@breaking
                    }
                }
            }
        }
    }

    fun filterInstance(instances: List<ServiceInstance>): MutableList<ServiceInstance> {
        return instances.filter { instance ->
            !breakers.containsKey(compositeKey(instance))
        }.toMutableList()
    }

    fun addFailRequest(url: String) {
        val key = compositeKey(url)
        val lock = getLock(key)

        try {
            lock.lock()
            var failRequest = failRequests[key] ?: FailRequest(0, LocalDateTime.now())

            // reset fail request count because of time difference over request window
            if (LocalDateTime.now().difference(failRequest.failTime) > defaultRequestWindow) {
                failRequest = FailRequest(0, LocalDateTime.now())
            }

            failRequest.failTimes++
            if (failRequest.failTimes >= requestVolumeThreshold!!) {
                logger.info("circuit add key($key) period($sleepWindowInSeconds)")
                breakers[key] = LocalDateTime.now().plusSeconds(sleepWindowInSeconds!!)
            }

            failRequests[key] = failRequest
        } finally {
            lock.unlock()
        }
    }

    private fun getLock(key: String): ReentrantLock {
        val lamada = { ReentrantLock() }
        return locks.getOrPut(key, lamada)
    }

    private fun compositeKey(url: String): String {
        val uri = URI.create(url)
        return "${uri.host}:${uri.port}"
    }

    private fun compositeKey(serviceInstance: ServiceInstance): String {
        return "${serviceInstance.host}:${serviceInstance.port}"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    data class FailRequest(
        var failTimes: Long,
        var failTime: LocalDateTime
    )
}