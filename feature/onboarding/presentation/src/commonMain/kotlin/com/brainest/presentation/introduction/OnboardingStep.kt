package com.brainest.presentation.introduction

import brainest.feature.onboarding.presentation.generated.resources.Res
import brainest.feature.onboarding.presentation.generated.resources.chatai
import brainest.feature.onboarding.presentation.generated.resources.flashquiz
import brainest.feature.onboarding.presentation.generated.resources.onboarding_step1_desc
import brainest.feature.onboarding.presentation.generated.resources.onboarding_step1_title
import brainest.feature.onboarding.presentation.generated.resources.onboarding_step2_desc
import brainest.feature.onboarding.presentation.generated.resources.onboarding_step2_title
import brainest.feature.onboarding.presentation.generated.resources.onboarding_step3_desc
import brainest.feature.onboarding.presentation.generated.resources.onboarding_step3_title
import brainest.feature.onboarding.presentation.generated.resources.snap
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.DrawableResource

data class OnboardingStep(
    val imageRes: DrawableResource,
    val titleRes: StringResource,
    val descriptionRes: StringResource
)

val onboardingPages = listOf(
    OnboardingStep(
        Res.drawable.chatai,
        Res.string.onboarding_step1_title,
        Res.string.onboarding_step1_desc
    ),
    OnboardingStep(
        Res.drawable.snap,
        Res.string.onboarding_step2_title,
        Res.string.onboarding_step2_desc
    ),
    OnboardingStep(
        Res.drawable.flashquiz,
        Res.string.onboarding_step3_title,
        Res.string.onboarding_step3_desc
    )


)
