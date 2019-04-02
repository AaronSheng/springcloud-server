package com.aaron.common.service

import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import kotlin.reflect.KClass

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
object MircoServiceApplication {
    fun run(application: KClass<*>, args: Array<String>) {
        SpringApplicationBuilder().bannerMode(Banner.Mode.OFF).sources(application.java).run(*args)
    }
}