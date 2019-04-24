package com.aaron.common.client.circuit

import com.aaron.common.service.util.SpringContextUtil
import feign.RetryableException
import feign.Retryer
import feign.Target
import java.net.SocketTimeoutException

/**
 * Created by Aaron Sheng on 2019/4/23.
 */
class NeverRetryer<T> constructor(private val target: Target<T>): Retryer {
    override fun clone(): Retryer {
        return this
    }

    override fun continueOrPropagate(e: RetryableException) {
        if (e.cause != null && e.cause is SocketTimeoutException) {
            val circuit = SpringContextUtil.getBean(Circuit::class.java)
            circuit.addCircuitInstance(target.url())
        }
        throw e
    }
}