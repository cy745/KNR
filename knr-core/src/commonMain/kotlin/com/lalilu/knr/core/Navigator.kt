package com.lalilu.knr.core

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No Navigator provided") }

class Navigator(
    val controller: NavHostController
) {

}