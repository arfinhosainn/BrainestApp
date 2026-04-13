package com.scelio.brainest.data.util

import com.scelio.brainest.domain.util.DataError
import com.scelio.brainest.domain.util.mapHttpStatusCodeToDataError
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.TimeoutCancellationException

/**
 * Maps network exceptions to [DataError.Remote] enum values.
 *
 * This handles Supabase REST errors, Ktor socket/HTTP errors, and general network failures.
 */
fun Exception.toDataError(): DataError.Remote {
    return when (this) {
        is RestException -> {
            mapHttpStatusCodeToDataError(statusCode)
        }

        is UnknownRestException -> DataError.Remote.UNKNOWN
        is ConnectTimeoutException -> DataError.Remote.SERVER_ERROR
        is SocketTimeoutException -> DataError.Remote.REQUEST_TIMEOUT
        is HttpRequestTimeoutException -> DataError.Remote.REQUEST_TIMEOUT
        is TimeoutCancellationException -> DataError.Remote.REQUEST_TIMEOUT
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

/**
 * Maps [HttpStatusCode] to [DataError.Remote] enum values.
 *
 * Delegates to the domain function [mapHttpStatusCodeToDataError].
 */
fun HttpStatusCode.toDataError(): DataError.Remote {
    return mapHttpStatusCodeToDataError(value)
}
