package com.lalilu.knr.core.ext

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.lalilu.knr.core.Transition
import kotlin.reflect.KType

inline fun <reified T : Any> NavGraphBuilder.knrComposable(
    transition: Transition? = null,
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)
) {
    composable<T>(
        deepLinks = deepLinks,
        typeMap = typeMap,
        enterTransition = transition?.enter(),
        exitTransition = transition?.exit(),
        popEnterTransition = transition?.popEnter(),
        popExitTransition = transition?.popExit(),
        sizeTransform = transition?.sizeTransform(),
        content = content
    )
}

internal fun paramsFromPath(path: String): Map<String, String> {
    return path.takeIf { it.contains('?') }
        ?.substringAfterLast('?')
        ?.split('&')
        ?.mapNotNull {
            val list = it.split('=')
                .takeIf { it.isNotEmpty() }
                ?: return@mapNotNull null

            list[0] to (list.getOrNull(1) ?: "")
        }?.toMap()
        ?: emptyMap()
}