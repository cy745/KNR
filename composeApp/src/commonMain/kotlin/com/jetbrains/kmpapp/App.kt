package com.jetbrains.kmpapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jetbrains.kmpapp.screens.detail.DetailScreen
import com.jetbrains.kmpapp.screens.list.ListScreen
import com.lalilu.knr.core.DeepLink
import com.lalilu.knr.core.LocalNavigator
import com.lalilu.knr.core.Navigator
import com.lalilu.knr.core.Screen
import com.lalilu.knr.core.annotation.Destination
import kotlinx.serialization.Serializable

@Serializable
@Destination(
    route = "/list",
    deeplink = [DeepLink(path = "/home")]
)
data object ListDestination : Screen {

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle) {
        val navigator = LocalNavigator.current

        ListScreen(navigateToDetails = { objectId ->
            navigator.controller.navigate(DetailDestination(objectId))
        })
    }
}

@Serializable
@Destination("/list/{objectId}")
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

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            val navController: NavHostController = rememberNavController()
            val navigator = remember(navController) { Navigator(navController) }

            CompositionLocalProvider(LocalNavigator provides navigator) {
                NavHost(navController = navController, startDestination = ListDestination) {
                    inject()
                }
            }
        }
    }
}

fun NavGraphBuilder.inject() {
    composable<ListDestination> { backStackEntry ->
        backStackEntry.toRoute<ListDestination>()
            .Content(backStackEntry.savedStateHandle)
    }
    composable<DetailDestination> { backStackEntry ->
        backStackEntry.toRoute<DetailDestination>()
            .Content(backStackEntry.savedStateHandle)
    }
}