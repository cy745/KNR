package com.jetbrains.kmpapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jetbrains.kmpapp.screens.detail.DetailScreen
import com.jetbrains.kmpapp.screens.list.ListScreen
import com.lalilu.knr.core.Screen
import kotlinx.serialization.Serializable

@Serializable
object ListDestination : Screen {

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle) {
        val navController = LocalNavigator.current

        ListScreen(navigateToDetails = { objectId ->
            navController.navigate(DetailDestination(objectId))
        })
    }
}

@Serializable
data class DetailDestination(val objectId: Int) : Screen {

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle) {
        val navController = LocalNavigator.current

        DetailScreen(
            objectId = objectId,
            navigateBack = {
                navController.popBackStack()
            }
        )
    }
}

val LocalNavigator = staticCompositionLocalOf<NavController> { error("No Navigator provided") }

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            val navController: NavHostController = rememberNavController()
            CompositionLocalProvider(LocalNavigator provides navController) {
                NavHost(navController = navController, startDestination = ListDestination) {
                    composableBind<ListDestination>(this)
                    composableBind<DetailDestination>(this)
                }
            }
        }
    }
}

inline fun <reified T : Screen> composableBind(builder: NavGraphBuilder) {
    builder.composable<T> { backStackEntry ->
        backStackEntry.toRoute<T>().Content(backStackEntry.savedStateHandle)
    }
}