package com.aaron.common.client.circuit

import feign.Request
import feign.RequestTemplate

/**
 * Created by Aaron Sheng on 2019/4/23.
 */
class RefreshableTarget<T> constructor(
    private val clz: Class<T>,
    private var url: String? = null
): AbstractTarget<T>() {

    fun refresh() {
        url = getService(clz)
    }

    override fun type(): Class<T> {
        return clz
    }

    override fun name(): String {
        return url()
    }

    override fun url(): String {
        if (url == null) {
            url = getService(clz)
        }
        return url!!
    }

    override fun apply(input: RequestTemplate): Request {
        if (input.url().indexOf("http") != 0) {
            input.target(url())
        }
        return input.request()
    }
}