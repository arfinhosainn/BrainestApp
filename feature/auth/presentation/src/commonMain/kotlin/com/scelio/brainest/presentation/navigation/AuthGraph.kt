package com.scelio.brainest.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.scelio.brainest.presentation.register.RegisterRoot
import com.scelio.brainest.presentation.register_success.RegisterSuccessRoot

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit,
) {
    navigation<AuthGraphRoutes.Graph>(
        startDestination = AuthGraphRoutes.Register
    ) {
        composable<AuthGraphRoutes.Register> {
            RegisterRoot(
                onRegisterSuccess = {
                    navController.navigate(AuthGraphRoutes.RegisterSuccess(it))
                }
            )
        }
        composable<AuthGraphRoutes.RegisterSuccess> {
            RegisterSuccessRoot()
        }
    }
}