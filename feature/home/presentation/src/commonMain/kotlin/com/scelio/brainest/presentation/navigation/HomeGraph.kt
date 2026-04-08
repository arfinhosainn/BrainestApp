package com.scelio.brainest.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scelio.brainest.presentation.home.HomeRoute
import kotlinx.serialization.Serializable

sealed interface HomeGraphRoutes {
    @Serializable data object Graph : HomeGraphRoutes
    @Serializable data object Home : HomeGraphRoutes
}

fun NavGraphBuilder.homeGraph() {
    navigation<HomeGraphRoutes.Graph>(
        startDestination = HomeGraphRoutes.Home
    ) {
        composable<HomeGraphRoutes.Home> {
            HomeRoute(
                onSettingsClick = {},
                onNotificationsClick = {},
            )
        }
    }
}
