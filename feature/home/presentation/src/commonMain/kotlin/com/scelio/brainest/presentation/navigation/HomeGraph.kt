package com.scelio.brainest.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scelio.brainest.presentation.home.HomeScreen
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
            HomeScreen(
                userName = "Student",
                decks = 12,
                quizzes = 4,
                others = 3,
                notificationCount = 1,
                onSettingsClick = {},
                onNotificationsClick = {},
            )
        }
    }
}
