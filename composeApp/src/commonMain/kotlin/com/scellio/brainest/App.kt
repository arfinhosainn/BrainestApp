package com.scellio.brainest

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brainest.presentation.navigation.OnboardingGraphRoutes
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.presentation.navigation.AuthGraphRoutes
import com.scelio.brainest.presentation.navigation.ChatGraphRoutes
import com.scelio.brainest.presentation.navigation.FlashcardsGraphRoutes
import com.scelio.brainest.presentation.navigation.HomeGraphRoutes
import com.scelio.brainest.presentation.util.ObserveAsEvents
import com.scellio.brainest.navigation.BrainestBottomNavigationBar
import com.scellio.brainest.navigation.DeepLinkListener
import com.scellio.brainest.navigation.NavigationRoot
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    onAutheticationChecked: () -> Unit = {},
    viewModel: MainViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    DeepLinkListener(navController)

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCheckingAuth) {
        if (!state.isCheckingAuth) {
            onAutheticationChecked()
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MainEvent.OnSessionExpired -> {
                navController.navigate(AuthGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = false
                    }
                }
            }
        }
    }

    BrainestTheme {
        if (!state.isCheckingAuth) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val hideBottomBar = currentRoute == FlashcardsGraphRoutes.AudioRecording::class.qualifiedName ||
                currentRoute?.endsWith(".AudioRecording") == true
            val mainGraphPrefixes = listOfNotNull(
                HomeGraphRoutes::class.qualifiedName,
                ChatGraphRoutes::class.qualifiedName,
                FlashcardsGraphRoutes::class.qualifiedName
            )
            val isMainGraphRoute = mainGraphPrefixes.any { prefix ->
                currentRoute?.startsWith(prefix) == true
            }
            val showBottomBar = (state.isLoggedIn || isMainGraphRoute) && !hideBottomBar
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        BrainestBottomNavigationBar(
                            onItemSelected = { index ->
                                when (index) {
                                    0 -> navController.navigate(HomeGraphRoutes.Graph) {
                                        launchSingleTop = true
                                    }
                                    1 -> navController.navigate(ChatGraphRoutes.Graph) {
                                        launchSingleTop = true
                                    }
                                    3 -> navController.navigate(FlashcardsGraphRoutes.Graph) {
                                        launchSingleTop = true
                                    }
                                    else -> Unit
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                NavigationRoot(
                    navController = navController,
                    startDestination = if (state.isLoggedIn) {
                        HomeGraphRoutes.Graph
                    } else {
                        OnboardingGraphRoutes.Graph
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
