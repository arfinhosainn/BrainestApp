package com.scelio.brainest.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.scelio.brainest.presentation.email_verification.EmailVerificationRoot
import com.scelio.brainest.presentation.register.RegisterRoot
import com.scelio.brainest.presentation.register_success.RegisterSuccessRoot

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit,
) {
    navigation<AuthGraphRoutes.Graph>(
        startDestination = AuthGraphRoutes.Login
    ) {

        composable<AuthGraphRoutes.Login> {
            LoginRoot(
                onLoginSuccess = onLoginSuccess,
                onForgotPasswordClick = {
                    navController.navigate(AuthGraphRoutes.ForgotPassword)
                },
                onCreateAccountClick = {
                    navController.navigate(AuthGraphRoutes.Register) {
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }


        composable<AuthGraphRoutes.Register> {
            RegisterRoot(
                onRegisterSuccess = { email ->
                    navController.navigate(AuthGraphRoutes.RegisterSuccess(email))
                }


            )
        }
        composable<AuthGraphRoutes.RegisterSuccess> { entry ->
            val route = entry.toRoute<AuthGraphRoutes.RegisterSuccess>()
            val email = route.email
            RegisterSuccessRoot()
        }
        composable<AuthGraphRoutes.EmailVerification>(
            deepLinks = listOf(
                navDeepLink<AuthGraphRoutes.EmailVerification>(
                    basePath = "brainest://brainest.app"
                ),
                navDeepLink<AuthGraphRoutes.EmailVerification>(
                    basePath = "https://brainest.app/auth/verify"
                )
            )
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<AuthGraphRoutes.EmailVerification>()
            val deepLinkUrl = route.deepLinkUrl
            EmailVerificationRoot()
        }
    }
}