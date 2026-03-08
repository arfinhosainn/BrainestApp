package com.brainest.presentation.introduction

import brainest.feature.onboarding.presentation.generated.resources.Res
import brainest.feature.onboarding.presentation.generated.resources.chatai
import brainest.feature.onboarding.presentation.generated.resources.flashquiz
import brainest.feature.onboarding.presentation.generated.resources.snap
import org.jetbrains.compose.resources.DrawableResource

data class OnboardingStep(
    val imageRes: DrawableResource,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingStep(
        Res.drawable.chatai,
        "Chat with Your AI Tutor",
        "Ask questions and get personalized help with math, science, and more."
    ),
    OnboardingStep(
        Res.drawable.snap,
        "Snap & Solve Questions",
        "Take a photo of any question and get instant step by step AI explanations."
    ),
    OnboardingStep(
        Res.drawable.flashquiz,
        "Create Flashcards & Quizzes",
        "Turn lessons into flashcards and quick quizzes to test your knowledge."
    )


)