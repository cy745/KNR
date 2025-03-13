package com.lalilu.knr.core.annotation

import com.lalilu.knr.core.DeepLink
import kotlin.reflect.KClass

/**
 * 注意: 使用此注解的类必须附带 @[Serializable] 注解。
 * 路由目标定义
 *
 * @param route 路由路径，例如：/home
 * @param routes 路由路径数组，例如：["/home", "/home/detail"]
 * @param deeplink 跳转链接，例如：[DeepLink(path = "/home")]
 * @param transition 跳转动画
 * @param remark 备注
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Destination(
    val route: String,
    val routes: Array<String> = [],
    val deeplink: Array<DeepLink> = [],
    val transition: KClass<*> = Unit::class,
    val remark: String = "",
    val start: Boolean = false
)