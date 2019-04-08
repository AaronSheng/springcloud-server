package com.aaron.common.api.annotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ServiceInterface(val value: String)