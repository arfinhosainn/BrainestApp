package com.scellio.brainest

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.brainest.presentation.navigation.OnboardingGraphRoutes
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.presentation.navigation.AuthGraphRoutes
import com.scelio.brainest.presentation.navigation.ChatGraphRoutes
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
            Scaffold(
                bottomBar = {
                    if (state.isLoggedIn) {
                        BrainestBottomNavigationBar()
                    }
                }
            ) { innerPadding ->
                NavigationRoot(
                    navController = navController,
                    startDestination = if (state.isLoggedIn) {
                        ChatGraphRoutes.Graph
                    } else {
                        OnboardingGraphRoutes.Graph
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
