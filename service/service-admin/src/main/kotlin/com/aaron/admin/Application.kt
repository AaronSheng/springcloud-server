package com.aaron.admin

import com.aaron.common.service.MicroService
import com.aaron.common.service.MircoServiceApplication
import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
@MicroService
@EnableAdminServer
@EnableDiscoveryClient
class Application

fun main(args: Array<String>) {
    MircoServiceApplication.run(Application::class, args)
}