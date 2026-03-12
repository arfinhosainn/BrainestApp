package com.brainest.presentation.onboarding

enum class OnboardingStepId {
    Name,
    Grade,
    Subjects,
    Goal,
    Challenges,
    Survey,
    LearningMethod,
    StudyTime,
    Language
}

object OnboardingFlowSteps {
    private val orderedSteps = listOf(
        OnboardingStepId.Name,
        OnboardingStepId.Grade,
        OnboardingStepId.Subjects,
        OnboardingStepId.Goal,
        OnboardingStepId.Challenges,
        OnboardingStepId.Survey,
        OnboardingStepId.LearningMethod,
        OnboardingStepId.StudyTime,
        OnboardingStepId.Language
    )

    val totalSteps: Int = orderedSteps.size

    fun indexOf(stepId: OnboardingStepId): Int {
        return orderedSteps.indexOf(stepId) + 1
    }
}
