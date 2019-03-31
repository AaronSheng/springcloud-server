package com.aaron.config

import com.aaron.common.service.MicroService
import com.aaron.common.service.MircoServiceApplication
import org.springframework.cloud.config.server.EnableConfigServer

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
@MicroService
@EnableConfigServer
class Application

fun main(args: Array<String>) {
    MircoServiceApplication.run(Application::class, args)
}