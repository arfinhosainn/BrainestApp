package com.brainest.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface OnboardingGraphRoutes {
    @Serializable
    data object Graph : OnboardingGraphRoutes

    @Serializable
    data object Welcome : OnboardingGraphRoutes

    @Serializable
    data object Onboarding : OnboardingGraphRoutes
}