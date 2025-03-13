package com.jetbrains.kmpapp.routes

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import com.jetbrains.kmpapp.screens.detail.DetailScreen
import com.lalilu.knr.core.LocalNavigator
import com.lalilu.knr.core.Screen
import com.lalilu.knr.core.annotation.Destination
import kotlinx.serialization.Serializable

@Serializable
@Destination(
    route = "/list/{objectId}",
    routes = ["/list/test/", "/list/test2"],
    transition = DefaultTransition::class,
)
data class DetailDestination(val objectId: Int) : Screen {

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle) {
        val navigator = LocalNavigator.current

        DetailScreen(
            objectId = objectId,
            navigateBack = { navigator.controller.popBackStack() }
        )
    }
}