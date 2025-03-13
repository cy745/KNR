package com.lalilu.knr.core

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No Navigator provided") }

class Navigator(
    val provider: Provider,
    val controller: NavHostController
) {
    fun navigate(route: String, params: Map<String, Any?> = emptyMap()) {
        val routeWithParams = runCatching { provider.getRoute(route) }
            .getOrElse {
                it.printStackTrace()
                null
            }

        val screen = routeWithParams?.let { it.first.invoke(params + it.second) }
            ?: return

        controller.navigate(screen)
    }
}