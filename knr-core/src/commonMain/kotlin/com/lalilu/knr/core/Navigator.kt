package com.lalilu.knr.core

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No Navigator provided") }

class Navigator(
    val provider: Provider,
    val controller: NavHostController
) {
    fun navigate(route: String, params: Map<String, Any?> = emptyMap()) {
        val route = runCatching { provider.getRoute(route) }
            .getOrElse {
                it.printStackTrace()
                null
            }

        val screen = route?.invoke(params)
            ?: return

        controller.navigate(screen)
    }
}