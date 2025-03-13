package com.jetbrains.kmpapp.routes

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import com.lalilu.knr.core.Transition
import com.lalilu.knr.core.TransitionFunc

object DefaultTransition : Transition {
    override fun enter(): TransitionFunc<EnterTransition>? = {
        slideInVertically(initialOffsetY = { fullHeight -> fullHeight })
    }

    override fun exit(): TransitionFunc<ExitTransition>? = {
        slideOutVertically(targetOffsetY = { it })
    }

    override fun popEnter(): TransitionFunc<EnterTransition>? = {
        slideInVertically(initialOffsetY = { fullHeight -> fullHeight })
    }

    override fun popExit(): TransitionFunc<ExitTransition>? = {
        slideOutVertically(targetOffsetY = { it })
    }
}