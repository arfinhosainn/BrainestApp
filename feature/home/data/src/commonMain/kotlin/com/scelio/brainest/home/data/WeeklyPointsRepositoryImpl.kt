package com.scelio.brainest.home.data

import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import com.scelio.brainest.data.util.toDataError
import com.scelio.brainest.flashcards.database.StudyDao
import com.scelio.brainest.flashcards.database.entities.WeeklyPointsEntity
import com.scelio.brainest.home.domain.WeeklyPointsRepository
import com.scelio.brainest.home.domain.WeeklyPointsSchedule
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class WeeklyPointsRepositoryImpl(
    private val supabase: SupabaseClient,
    private val logger: BrainestLogger,
    private val studyDao: StudyDao
) : WeeklyPointsRepository {

    override suspend fun getWeeklySchedule(
        userId: String,
        weekStartDate: LocalDate
    ): Result<WeeklyPointsSchedule?, DataError.Remote> {
        // Local-first: check local database first
        val localSchedule = studyDao.getWeeklyPoints(userId, weekStartDate.toString())
        if (localSchedule != null) {
            return Result.Success(localSchedule.toDomain())
        }

        // Fallback to remote
        return try {
            val schedules = supabase.from("user_weekly_points")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("week_start_date", weekStartDate.toString())
                    }
                }
                .decodeList<WeeklyPointsScheduleDto>()

            val schedule = schedules.firstOrNull()
            // Cache to local database
            schedule?.let {
                studyDao.upsertWeeklyPoints(it.toEntity())
            }
            Result.Success(schedule?.toDomain())
        } catch (e: Exception) {
            logger.error("[WeeklyPointsRepository] Failed to fetch weekly schedule: ${e.message}")
            Result.Failure(e.toDataError())
        }
    }

    override suspend fun saveWeeklySchedule(
        schedule: WeeklyPointsSchedule
    ): EmptyResult<DataError.Remote> {
        // Save locally first
        studyDao.upsertWeeklyPoints(schedule.toEntity())

        // Then sync to remote
        return try {
            val dto = schedule.toDto()
            supabase.from("user_weekly_points").upsert(dto)
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.error("[WeeklyPointsRepository] Failed to save weekly schedule to remote: ${e.message}")
            // Local save succeeded, so return success even if remote fails
            Result.Success(Unit)
        }
    }
}

// Local mapper functions
private fun WeeklyPointsSchedule.toEntity(): WeeklyPointsEntity {
    return WeeklyPointsEntity(
        userId = userId,
        weekStartDate = weekStartDate.toString(),
        mondayPoints = mondayPoints,
        tuesdayPoints = tuesdayPoints,
        wednesdayPoints = wednesdayPoints,
        thursdayPoints = thursdayPoints,
        fridayPoints = fridayPoints,
        saturdayPoints = saturdayPoints,
        sundayPoints = sundayPoints,
        createdAt = "",
        updatedAt = ""
    )
}

private fun WeeklyPointsEntity.toDomain(): WeeklyPointsSchedule {
    return WeeklyPointsSchedule(
        userId = userId,
        weekStartDate = LocalDate.parse(weekStartDate),
        mondayPoints = mondayPoints,
        tuesdayPoints = tuesdayPoints,
        wednesdayPoints = wednesdayPoints,
        thursdayPoints = thursdayPoints,
        fridayPoints = fridayPoints,
        saturdayPoints = saturdayPoints,
        sundayPoints = sundayPoints
    )
}

@Serializable
data class WeeklyPointsScheduleDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("week_start_date")
    val weekStartDate: String,
    @SerialName("monday_points")
    val mondayPoints: Int,
    @SerialName("tuesday_points")
    val tuesdayPoints: Int,
    @SerialName("wednesday_points")
    val wednesdayPoints: Int,
    @SerialName("thursday_points")
    val thursdayPoints: Int,
    @SerialName("friday_points")
    val fridayPoints: Int,
    @SerialName("saturday_points")
    val saturdayPoints: Int,
    @SerialName("sunday_points")
    val sundayPoints: Int
)

fun WeeklyPointsScheduleDto.toDomain(): WeeklyPointsSchedule {
    return WeeklyPointsSchedule(
        userId = userId,
        weekStartDate = LocalDate.parse(weekStartDate),
        mondayPoints = mondayPoints,
        tuesdayPoints = tuesdayPoints,
        wednesdayPoints = wednesdayPoints,
        thursdayPoints = thursdayPoints,
        fridayPoints = fridayPoints,
        saturdayPoints = saturdayPoints,
        sundayPoints = sundayPoints
    )
}

fun WeeklyPointsScheduleDto.toEntity(): WeeklyPointsEntity {
    return WeeklyPointsEntity(
        userId = userId,
        weekStartDate = weekStartDate,
        mondayPoints = mondayPoints,
        tuesdayPoints = tuesdayPoints,
        wednesdayPoints = wednesdayPoints,
        thursdayPoints = thursdayPoints,
        fridayPoints = fridayPoints,
        saturdayPoints = saturdayPoints,
        sundayPoints = sundayPoints,
        createdAt = "",
        updatedAt = ""
    )
}

fun WeeklyPointsSchedule.toDto(): WeeklyPointsScheduleDto {
    return WeeklyPointsScheduleDto(
        userId = userId,
        weekStartDate = weekStartDate.toString(),
        mondayPoints = mondayPoints,
        tuesdayPoints = tuesdayPoints,
        wednesdayPoints = wednesdayPoints,
        thursdayPoints = thursdayPoints,
        fridayPoints = fridayPoints,
        saturdayPoints = saturdayPoints,
        sundayPoints = sundayPoints
    )
}
