package com.brainest.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.brainest.presentation.introduction.OnboardingScreen
import com.brainest.presentation.introduction.WelcomeScreen

fun NavGraphBuilder.onboardingGraph(navController: NavController) {
    navigation<OnboardingGraphRoutes.Graph>(
        startDestination = OnboardingGraphRoutes.Welcome
    ) {
        composable<OnboardingGraphRoutes.Welcome> {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(OnboardingGraphRoutes.Onboarding)
                }
            )
        }

        composable<OnboardingGraphRoutes.Onboarding>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 1200)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) {
            OnboardingScreen(onFinishOnboarding = {})
        }
    }
}