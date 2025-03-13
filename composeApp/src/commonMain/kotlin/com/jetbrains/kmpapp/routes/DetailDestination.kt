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
data class DetailDestination(val objectId: String) : Screen {

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle) {
        val navigator = LocalNavigator.current

        DetailScreen(
            objectId = objectId.toIntOrNull() ?: 0, // TODO 待实现自动将路由中的String转化成函数参数类型的逻辑
            navigateBack = { navigator.controller.popBackStack() }
        )
    }
}