package com.scelio.brainest.presentation.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.scelio.brainest.presentation.SettingScreen
import com.scelio.brainest.presentation.home.HomeRoute
import com.scelio.brainest.presentation.settings.SettingsViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

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
            val viewModel: SettingsViewModel = koinViewModel()
            val state = viewModel.state.collectAsStateWithLifecycle().value
            
            SettingScreen(
                name = state.username,
                joinedText = state.joinedText,
                onBackClick = {
                    navController.popBackStack()
                },
                onNameChange = { newName ->
                    viewModel.onNameChange(newName)
                },
                onLogoutClick = {
                    viewModel.onLogoutClick()
                    navController.popBackStack()
                }
            )
        }
    }
}
