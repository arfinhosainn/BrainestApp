package com.scelio.brainest.data.onboarding

import com.scelio.brainest.domain.logging.BrainestLogger
import com.scelio.brainest.domain.onboarding.OnboardingStore
import com.scelio.brainest.domain.onboarding.OnboardingSyncer
import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.EmptyResult
import com.scelio.brainest.domain.util.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.postgrest.from
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
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

private fun Exception.toDataError(): DataError.Remote {
    return when (this) {
        is RestException -> {
            when (statusCode) {
                400 -> DataError.Remote.BAD_REQUEST
                401 -> DataError.Remote.UNAUTHORIZED
                403 -> DataError.Remote.FORBIDDEN
                404 -> DataError.Remote.NOT_FOUND
                408 -> DataError.Remote.REQUEST_TIMEOUT
                409 -> DataError.Remote.CONFLICT
                413 -> DataError.Remote.PAYLOAD_TOO_LARGE
                429 -> DataError.Remote.TOO_MANY_REQUESTS
                in 500..502 -> DataError.Remote.SERVER_ERROR
                503 -> DataError.Remote.SERVICE_UNAVAILABLE
                else -> DataError.Remote.UNKNOWN
            }
        }

        is UnknownRestException -> DataError.Remote.UNKNOWN
        is ConnectTimeoutException -> DataError.Remote.SERVER_ERROR
        is SocketTimeoutException -> DataError.Remote.REQUEST_TIMEOUT
        else -> {
            if (message?.contains("Unable to resolve host") == true ||
                message?.contains("Network is unreachable") == true
            ) {
                DataError.Remote.NO_INTERNET
            } else {
                DataError.Remote.UNKNOWN
            }
        }
    }
}
