package com.lalilu.knr.core

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Stable
import androidx.navigation.NavBackStackEntry
import kotlin.jvm.JvmSuppressWildcards

typealias TransitionScope = AnimatedContentTransitionScope<NavBackStackEntry>
typealias TransitionFunc<T> = TransitionScope.() -> @JvmSuppressWildcards T?

interface Transition {
    @Stable
    fun enter(): TransitionFunc<EnterTransition>? = null

    @Stable
    fun exit(): TransitionFunc<ExitTransition>? = null

    @Stable
    fun popEnter(): TransitionFunc<EnterTransition>? = null

    @Stable
    fun popExit(): TransitionFunc<ExitTransition>? = null

    @Stable
    fun sizeTransform(): TransitionFunc<SizeTransform>? = null
}