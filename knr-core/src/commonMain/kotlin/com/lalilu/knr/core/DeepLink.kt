package com.lalilu.knr.core

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DeepLink(
    val path: String,
    val action: String = "",
    val mimeType: String = "",
    val through: Array<String> = [],
    val transition: KClass<*> = Unit::class
)