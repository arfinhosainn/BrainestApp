package com.scelio.brainest.home.domain

import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.Result

interface AchievementsRepository {
    suspend fun getUserAchievements(
        userId: String
    ): Result<UserAchievements?, DataError.Remote>

    suspend fun upsertUserAchievements(
        achievements: UserAchievements
    ): EmptyResult<DataError.Remote>

    suspend fun insertAchievementEvents(
        events: List<UserAchievementEventInput>
    ): EmptyResult<DataError.Remote>
}
