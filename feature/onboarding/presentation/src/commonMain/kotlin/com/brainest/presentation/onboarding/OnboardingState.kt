package com.brainest.presentation.onboarding

data class OnboardingState(
    val name: String = "",
    val gradeId: String? = null,
    val subjectIds: Set<String> = emptySet(),
    val goalId: String? = null,
    val challengeIds: Set<String> = emptySet(),
    val studyHours: Int = 2,
    val learningMethodId: String? = null,
    val studyTimeId: String? = null,
    val languageId: String? = null
)
