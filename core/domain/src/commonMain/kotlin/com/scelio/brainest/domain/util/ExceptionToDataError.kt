package com.scelio.brainest.domain.util

/**
 * Maps HTTP status codes to [DataError.Remote] enum values.
 *
 * This is a pure domain function with no external dependencies.
 */
fun mapHttpStatusCodeToDataError(statusCode: Int): DataError.Remote {
    return when (statusCode) {
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
