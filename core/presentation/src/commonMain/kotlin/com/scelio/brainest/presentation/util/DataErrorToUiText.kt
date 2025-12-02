package com.scelio.brainest.presentation.util

import brainest.core.presentation.generated.resources.Res
import brainest.core.presentation.generated.resources.error_bad_request
import brainest.core.presentation.generated.resources.error_conflict
import brainest.core.presentation.generated.resources.error_disk_full
import brainest.core.presentation.generated.resources.error_forbidden
import brainest.core.presentation.generated.resources.error_no_internet
import brainest.core.presentation.generated.resources.error_not_found
import brainest.core.presentation.generated.resources.error_payload_too_large
import brainest.core.presentation.generated.resources.error_request_timeout
import brainest.core.presentation.generated.resources.error_serialization
import brainest.core.presentation.generated.resources.error_server
import brainest.core.presentation.generated.resources.error_service_unavailable
import brainest.core.presentation.generated.resources.error_too_many_requests
import brainest.core.presentation.generated.resources.error_unauthorized
import brainest.core.presentation.generated.resources.error_unknown
import com.scelio.brainest.domain.util.DataError


fun DataError.toUiText(): UiText {
    val resource = when (this) {
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.NOT_FOUND -> Res.string.error_not_found
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.BAD_REQUEST -> Res.string.error_bad_request
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.UNAUTHORIZED -> Res.string.error_unauthorized
        DataError.Remote.FORBIDDEN -> Res.string.error_forbidden
        DataError.Remote.NOT_FOUND -> Res.string.error_not_found
        DataError.Remote.CONFLICT -> Res.string.error_conflict
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.PAYLOAD_TOO_LARGE -> Res.string.error_payload_too_large
        DataError.Remote.SERVER_ERROR -> Res.string.error_server
        DataError.Remote.SERVICE_UNAVAILABLE -> Res.string.error_service_unavailable
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization
        DataError.Remote.UNKNOWN -> Res.string.error_unknown
    }
    return UiText.Resource(resource)
}