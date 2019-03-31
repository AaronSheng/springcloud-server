package com.aaron.server

import com.aaron.common.service.MicroService
import com.aaron.common.service.MircoServiceApplication

/**
 * Created by Aaron Sheng on 2018/6/19.
 */
@MicroService
class Application

fun main(args: Array<String>) {
    MircoServiceApplication.run(Application::class, args)
}