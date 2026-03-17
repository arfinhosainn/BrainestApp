package com.scelio.brainest.data.onboarding

import com.scelio.brainest.domain.onboarding.OnboardingData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray

@Serializable
data class OnboardingSupabaseDto(
    @SerialName("user_id")
    val userId: String,
    val name: String,
    @SerialName("grade_id")
    val gradeId: String? = null,
    @SerialName("subject_ids")
    val subjectIds: JsonArray = JsonArray(emptyList()),
    @SerialName("goal_id")
    val goalId: String? = null,
    @SerialName("challenge_ids")
    val challengeIds: JsonArray = JsonArray(emptyList()),
    @SerialName("study_hours")
    val studyHours: Int,
    @SerialName("learning_method_id")
    val learningMethodId: String? = null,
    @SerialName("study_time_id")
    val studyTimeId: String? = null,
    @SerialName("language_id")
    val languageId: String? = null
)

fun OnboardingData.toSupabaseDto(userId: String): OnboardingSupabaseDto {
    return OnboardingSupabaseDto(
        userId = userId,
        name = name,
        gradeId = gradeId,
        subjectIds = subjectIds.toJsonArray(),
        goalId = goalId,
        challengeIds = challengeIds.toJsonArray(),
        studyHours = studyHours,
        learningMethodId = learningMethodId,
        studyTimeId = studyTimeId,
        languageId = languageId
    )
}

private fun Set<String>.toJsonArray(): JsonArray {
    return buildJsonArray {
        for (item in this@toJsonArray) {
            add(item)
        }
    }
}
