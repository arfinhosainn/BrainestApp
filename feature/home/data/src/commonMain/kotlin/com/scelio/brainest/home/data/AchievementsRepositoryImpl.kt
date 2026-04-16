package com.scelio.brainest.home.data

import com.scelio.brainest.data.util.toDataError
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.flashcards.database.StudyDao
import com.scelio.brainest.flashcards.database.entities.UserAchievementsEntity
import com.scelio.brainest.home.domain.AchievementsRepository
import com.scelio.brainest.home.domain.UserAchievementEventInput
import com.scelio.brainest.home.domain.UserAchievementEventType
import com.scelio.brainest.home.domain.UserAchievements
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AchievementsRepositoryImpl(
    private val supabase: SupabaseClient,
    private val logger: BrainestLogger,
    private val studyDao: StudyDao
) : AchievementsRepository {

    override suspend fun getUserAchievements(
        userId: String
    ): Result<UserAchievements?, DataError.Remote> {
        val localAchievements = studyDao.getUserAchievements(userId)
        if (localAchievements != null) {
            return Result.Success(localAchievements.toDomain())
        }

        return try {
            val achievements = supabase.from("user_achievements")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<UserAchievementsDto>()

            val mapped = achievements.firstOrNull()?.toDomain()
            mapped?.let { studyDao.upsertUserAchievements(it.toEntity()) }
            Result.Success(mapped)
        } catch (e: Exception) {
            logger.error("[AchievementsRepository] Failed to load achievements: ${e.message}")
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun upsertUserAchievements(
        achievements: UserAchievements
    ): EmptyResult<DataError.Remote> {
        studyDao.upsertUserAchievements(achievements.toEntity())

        return try {
            supabase.from("user_achievements").upsert(achievements.toDto())
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("[AchievementsRepository] Failed to upsert achievements: ${e.message}")
            // Local update already succeeded, so keep UX resilient offline.
            Result.Success(Unit)
        }
    }

    override suspend fun insertAchievementEvents(
        events: List<UserAchievementEventInput>
    ): EmptyResult<DataError.Remote> {
        if (events.isEmpty()) return Result.Success(Unit)

        return try {
            supabase.from("user_achievement_events").insert(events.map { it.toDto() })
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("[AchievementsRepository] Failed to insert achievement events: ${e.message}")
            Result.Failure(e.toDataError())
        }
    }
}

@Serializable
private data class UserAchievementsDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("total_points")
    val totalPoints: Int,
    @SerialName("current_streak_days")
    val currentStreakDays: Int,
    @SerialName("longest_streak_days")
    val longestStreakDays: Int,
    @SerialName("completed_decks_count")
    val completedDecksCount: Int,
    @SerialName("completed_quizzes_count")
    val completedQuizzesCount: Int,
    @SerialName("last_activity_date")
    val lastActivityDate: String? = null
)

private fun UserAchievementsDto.toDomain(): UserAchievements {
    return UserAchievements(
        userId = userId,
        totalPoints = totalPoints,
        currentStreakDays = currentStreakDays,
        longestStreakDays = longestStreakDays,
        completedDecksCount = completedDecksCount,
        completedQuizzesCount = completedQuizzesCount,
        lastActivityDate = lastActivityDate?.let(LocalDate::parse)
    )
}

@Serializable
private data class UpsertUserAchievementsDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("total_points")
    val totalPoints: Int,
    @SerialName("current_streak_days")
    val currentStreakDays: Int,
    @SerialName("longest_streak_days")
    val longestStreakDays: Int,
    @SerialName("completed_decks_count")
    val completedDecksCount: Int,
    @SerialName("completed_quizzes_count")
    val completedQuizzesCount: Int,
    @SerialName("last_activity_date")
    val lastActivityDate: String? = null
)

@Serializable
private data class UserAchievementEventDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("event_type")
    val eventType: String,
    @SerialName("points_delta")
    val pointsDelta: Int? = null,
    @SerialName("related_deck_id")
    val relatedDeckId: String? = null,
    @SerialName("occurred_on")
    val occurredOn: String? = null,
    @SerialName("metadata")
    val metadata: String? = null
)

private fun UserAchievements.toDto(): UpsertUserAchievementsDto {
    return UpsertUserAchievementsDto(
        userId = userId,
        totalPoints = totalPoints,
        currentStreakDays = currentStreakDays,
        longestStreakDays = longestStreakDays,
        completedDecksCount = completedDecksCount,
        completedQuizzesCount = completedQuizzesCount,
        lastActivityDate = lastActivityDate?.toString()
    )
}

private fun UserAchievementEventInput.toDto(): UserAchievementEventDto {
    return UserAchievementEventDto(
        userId = userId,
        eventType = eventType.toDbValue(),
        pointsDelta = pointsDelta,
        relatedDeckId = relatedDeckId,
        occurredOn = occurredOn?.toString(),
        metadata = metadata
    )
}

private fun UserAchievementEventType.toDbValue(): String {
    return when (this) {
        UserAchievementEventType.POINTS_EARNED -> "POINTS_EARNED"
        UserAchievementEventType.STREAK_UPDATED -> "STREAK_UPDATED"
        UserAchievementEventType.DECK_COMPLETED -> "DECK_COMPLETED"
        UserAchievementEventType.QUIZ_COMPLETED -> "QUIZ_COMPLETED"
    }
}

private fun UserAchievementsEntity.toDomain(): UserAchievements {
    return UserAchievements(
        userId = userId,
        totalPoints = totalPoints,
        currentStreakDays = currentStreakDays,
        longestStreakDays = longestStreakDays,
        completedDecksCount = completedDecksCount,
        completedQuizzesCount = completedQuizzesCount,
        lastActivityDate = lastActivityDate?.let(LocalDate::parse)
    )
}

private fun UserAchievements.toEntity(): UserAchievementsEntity {
    return UserAchievementsEntity(
        userId = userId,
        totalPoints = totalPoints,
        currentStreakDays = currentStreakDays,
        longestStreakDays = longestStreakDays,
        completedDecksCount = completedDecksCount,
        completedQuizzesCount = completedQuizzesCount,
        lastActivityDate = lastActivityDate?.toString(),
        createdAt = "",
        updatedAt = ""
    )
}
