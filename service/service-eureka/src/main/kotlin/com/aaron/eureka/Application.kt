package com.aaron.eureka

import com.aaron.common.service.MicroService
import com.aaron.common.service.MircoServiceApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

/**
 * Created by Aaron Sheng on 2019/4/10.
 */
@MicroService
@EnableEurekaServer
class Application

fun main(args: Array<String>) {
    MircoServiceApplication.run(Application::class, args)
}