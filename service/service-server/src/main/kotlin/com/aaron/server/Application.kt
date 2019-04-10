package com.aaron.server

import com.aaron.common.service.MicroService
import com.aaron.common.service.MircoServiceApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
@MicroService
@EnableDiscoveryClient
@EnableFeignClients
class Application

fun main(args: Array<String>) {
    MircoServiceApplication.run(Application::class, args)
}