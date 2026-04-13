package com.scelio.brainest.data.onboarding

import com.scelio.brainest.data.util.toDataError
import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.onboarding.OnboardingStore
import com.scelio.brainest.domain.onboarding.OnboardingSyncer
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class OnboardingSyncerImpl(
    private val store: OnboardingStore,
    private val supabase: SupabaseClient,
    private val logger: BrainestLogger
) : OnboardingSyncer {

    override suspend fun sync(userId: String): EmptyResult<DataError.Remote> {
        return withContext(Dispatchers.IO) {
            val data = store.data.first()
            try {
                supabase.from("onboarding").upsert(data.toSupabaseDto(userId))
                logger.info("Onboarding sync success")
                Result.Success(Unit)
            } catch (e: Exception) {
                logger.error("Onboarding sync failed: ${e.message}")
                Result.Failure(e.toDataError())
            }
        }
    }
}
