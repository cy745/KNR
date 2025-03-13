package com.jetbrains.kmpapp.routes

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import com.jetbrains.kmpapp.screens.list.ListScreen
import com.lalilu.knr.core.DeepLink
import com.lalilu.knr.core.LocalNavigator
import com.lalilu.knr.core.Screen
import com.lalilu.knr.core.annotation.Destination
import kotlinx.serialization.Serializable

@Serializable
@Destination(
    route = "/list",
    deeplink = [DeepLink(path = "/home")],
    start = true
)
data object ListDestination : Screen {

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle) {
        val navigator = LocalNavigator.current

        ListScreen(navigateToDetails = { objectId ->
            navigator.navigate(route = "/list/$objectId")
        })
    }
}