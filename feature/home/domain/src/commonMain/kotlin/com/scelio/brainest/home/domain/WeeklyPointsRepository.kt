package com.scelio.brainest.home.domain

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import kotlinx.datetime.LocalDate

interface WeeklyPointsRepository {
    /**
     * Fetch the weekly points schedule for a user and week.
     * Returns null if no schedule exists for that week.
     */
    suspend fun getWeeklySchedule(
        userId: String,
        weekStartDate: LocalDate
    ): Result<WeeklyPointsSchedule?, DataError.Remote>

    /**
     * Save a weekly points schedule to the database.
     */
    suspend fun saveWeeklySchedule(
        schedule: WeeklyPointsSchedule
    ): EmptyResult<DataError.Remote>
}
