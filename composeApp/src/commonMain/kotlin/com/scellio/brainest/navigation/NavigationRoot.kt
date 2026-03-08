package com.scellio.brainest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.brainest.presentation.navigation.onboardingGraph
import com.scelio.brainest.presentation.navigation.AuthGraphRoutes
import com.scelio.brainest.presentation.navigation.ChatGraphRoutes
import com.scelio.brainest.presentation.navigation.authGraph
import com.scelio.brainest.presentation.navigation.chatGraph

@Suppress("ParamsComparedByRef")
@Composable
fun NavigationRoot(
    navController: NavHostController,
    startDestination: Any
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(ChatGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            }
        )
        chatGraph(
            navController = navController
        )
        onboardingGraph(
            navController = navController
        )
    }
}