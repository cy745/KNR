package com.lalilu.knr.core.annotation

import com.lalilu.knr.core.DeepLink
import kotlin.reflect.KClass

/**
 * 注意: 使用此注解的类必须附带 @[Serializable] 注解。
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Destination(
    val route: String,
    val routes: Array<String> = [],
    val deeplink: Array<DeepLink> = [],
    val transition: KClass<*> = Unit::class,
    val remark: String = ""
)