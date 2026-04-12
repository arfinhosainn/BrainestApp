package com.scelio.brainest.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scelio.brainest.presentation.SettingScreen
import com.scelio.brainest.presentation.home.HomeRoute
import kotlinx.serialization.Serializable

sealed interface HomeGraphRoutes {
    @Serializable data object Graph : HomeGraphRoutes
    @Serializable data object Home : HomeGraphRoutes
    @Serializable data object Settings : HomeGraphRoutes
}

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<HomeGraphRoutes.Graph>(
        startDestination = HomeGraphRoutes.Home
    ) {
        composable<HomeGraphRoutes.Home> {
            HomeRoute(
                onSettingsClick = {
                    navController.navigate(HomeGraphRoutes.Settings)
                },
                onNotificationsClick = {},
            )
        }

        composable<HomeGraphRoutes.Settings> {
            SettingScreen(
                name = "Student",
                joinedText = "Joined Oct 2017",
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
