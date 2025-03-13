package com.lalilu.knr.core

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.lalilu.knr.core.ext.paramsFromPath

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No Navigator provided") }

class Navigator(
    val provider: Provider,
    val controller: NavHostController,
    val exceptionHandler: ExceptionHandler = ExceptionHandler.DEFAULT,
    val interceptors: List<Interceptor> = emptyList()
) {
    fun route(path: String) = NavRequest(path = path)
    fun getScreen(route: String, params: Map<String, Any?> = emptyMap()): Screen {
        return provider.getRoute(route).let {
            it.first.invoke(params + it.second + paramsFromPath(route))
        }
    }

    inner class NavRequest internal constructor(
        val path: String,
        val params: MutableMap<String, Any?> = mutableMapOf()
    ) {
        fun with(name: String, value: Any?) = apply { params[name] = value }

        fun jump(block: NavOptionsBuilder.() -> Unit = {}) {
            runCatching {
                val screen = getScreen(path, params)
                controller.navigate(screen) { block() }
            }.getOrElse {
                exceptionHandler.onException(it)
            }
        }
    }
}