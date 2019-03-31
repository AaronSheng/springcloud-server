package com.aaron.common.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.archaius.ArchaiusAutoConfiguration
import org.springframework.cloud.netflix.rx.RxJavaAutoConfiguration

/**
 * Created by Aaron Sheng on 2019/3/31.
 */
@SpringBootApplication(exclude = [(ArchaiusAutoConfiguration::class), (RxJavaAutoConfiguration::class)])
annotation class MicroService