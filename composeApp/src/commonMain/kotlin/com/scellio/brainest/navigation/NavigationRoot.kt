package com.scellio.brainest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.scelio.brainest.presentation.navigation.AuthGraphRoutes
import com.scelio.brainest.presentation.navigation.authGraph

@Composable
fun NavigationRoot(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = AuthGraphRoutes.Graph
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {

            }
        )
    }
}