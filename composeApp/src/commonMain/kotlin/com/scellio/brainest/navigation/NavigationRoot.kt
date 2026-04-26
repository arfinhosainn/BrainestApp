package com.scellio.brainest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.brainest.presentation.navigation.OnboardingGraphRoutes
import com.brainest.presentation.navigation.onboardingGraph
import com.scelio.brainest.presentation.navigation.AuthGraphRoutes
import com.scelio.brainest.presentation.navigation.HomeGraphRoutes
import com.scelio.brainest.presentation.navigation.authGraph
import com.scelio.brainest.presentation.navigation.chatGraph
import com.scelio.brainest.presentation.navigation.flashcardsGraph
import com.scelio.brainest.presentation.navigation.homeGraph
import com.scelio.brainest.presentation.navigation.scanGraph

@Suppress("ParamsComparedByRef")
@Composable
fun NavigationRoot(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(HomeGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            }
        )
        homeGraph(
            navController = navController
        )
        chatGraph(
            navController = navController
        )
        flashcardsGraph(
            navController = navController
        )
        onboardingGraph(
            navController = navController,
            onFinishOnboarding = {
                navController.navigate(AuthGraphRoutes.Login) {
                    popUpTo(OnboardingGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            }
        )
        scanGraph()
    }
}
