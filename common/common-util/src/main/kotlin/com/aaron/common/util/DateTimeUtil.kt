package com.aaron.common.util

import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Created by Aaron Sheng on 2019/4/23.
 */
fun LocalDateTime.timestamp(): Long {
    val zoneId = ZoneId.systemDefault()
    return this.atZone(zoneId).toInstant().epochSecond
}

fun LocalDateTime.over(): Boolean {
    val now = LocalDateTime.now()
    return timestamp() < now.timestamp()
}

fun LocalDateTime.difference(other: LocalDateTime): Long {
    return timestamp() - other.timestamp()
}