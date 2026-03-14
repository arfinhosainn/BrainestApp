package com.brainest.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface OnboardingGraphRoutes {
    @Serializable
    data object Graph : OnboardingGraphRoutes

    @Serializable
    data object Welcome : OnboardingGraphRoutes

    @Serializable
    data object Introduction : OnboardingGraphRoutes

    @Serializable
    data object Name : OnboardingGraphRoutes

    @Serializable
    data object Grade : OnboardingGraphRoutes

    @Serializable
    data object Subjects : OnboardingGraphRoutes

    @Serializable
    data object Goal : OnboardingGraphRoutes

    @Serializable
    data object Challenges : OnboardingGraphRoutes

    @Serializable
    data object Survey : OnboardingGraphRoutes

    @Serializable
    data object LearningMethod : OnboardingGraphRoutes

    @Serializable
    data object StudyTime : OnboardingGraphRoutes

    @Serializable
    data object Language : OnboardingGraphRoutes

    @Serializable
    data object Growth : OnboardingGraphRoutes

    @Serializable
    data object Review : OnboardingGraphRoutes

    @Serializable
    data object Permissions : OnboardingGraphRoutes
}
