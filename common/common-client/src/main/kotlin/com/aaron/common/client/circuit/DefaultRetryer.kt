package com.aaron.common.client.circuit

import com.aaron.common.service.util.SpringContextUtil
import feign.RetryableException
import feign.Retryer
import java.net.SocketTimeoutException

/**
 * Created by Aaron Sheng on 2019/4/24.
 */
class DefaultRetryer<T> constructor(
    private val target: RefreshableTarget<T>,
    private var maxAttempts: Long,
    private var period: Long,
    private var maxPeriod: Long,
    private var attempt: Int = 0,
    private var sleptForMillis: Long = 0
): Retryer {

    constructor(target: RefreshableTarget<T>): this(target, 100, 1000, 5)

    override fun clone(): Retryer {
        return DefaultRetryer(target, period, maxPeriod, maxAttempts)
    }

    override fun continueOrPropagate(e: RetryableException) {
        // add url to circuit
        if (e.cause != null && e.cause is SocketTimeoutException) {
            val circuit = SpringContextUtil.getBean(Circuit::class.java)
            circuit.addCircuitInstance(target.url())
        }

        if (attempt++ >= maxAttempts) {
            throw e
        }

        var interval: Long
        if (e.retryAfter() != null) {
            interval = e.retryAfter().time - currentTimeMillis()
            if (interval > maxPeriod) {
                interval = maxPeriod
            }
            if (interval < 0) {
                return
            }
        } else {
            interval = nextMaxInterval()
        }
        try {
            Thread.sleep(interval)
        } catch (ignored: InterruptedException) {
            Thread.currentThread().interrupt()
            throw e
        }

        sleptForMillis += interval

        // fresh url to retry request
        target.refresh()
    }

    private fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Calculates the time interval to a retry attempt. <br></br>
     * The interval increases exponentially with each attempt, at a rate of nextInterval *= 1.5
     * (where 1.5 is the backoff factor), to the maximum interval.
     *
     * @return time in nanoseconds from now until the next attempt.
     */
    private fun nextMaxInterval(): Long {
        val interval = (period * Math.pow(1.5, (attempt - 1).toDouble())).toLong()
        return if (interval > maxPeriod) maxPeriod else interval
    }
}