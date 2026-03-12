package com.brainest.presentation.onboarding

sealed interface OnboardingAction {
    data class NameChanged(val value: String) : OnboardingAction
    data class GradeSelected(val id: String) : OnboardingAction
    data class SubjectToggled(val id: String) : OnboardingAction
    data object ClearSubjects : OnboardingAction
    data class GoalSelected(val id: String) : OnboardingAction
    data class ChallengeToggled(val id: String) : OnboardingAction
    data object ClearChallenges : OnboardingAction
    data class StudyHoursChanged(val hours: Int) : OnboardingAction
    data class LearningMethodSelected(val id: String) : OnboardingAction
    data class StudyTimeSelected(val id: String) : OnboardingAction
    data class LanguageSelected(val id: String) : OnboardingAction
    data object Reset : OnboardingAction
}
