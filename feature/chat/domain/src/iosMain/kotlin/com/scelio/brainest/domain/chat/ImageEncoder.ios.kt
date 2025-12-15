package com.scelio.brainest.domain.chat


import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create

actual object ImageEncoder {
    actual suspend fun encodeToBase64(bytes: ByteArray, mimeType: String): String {
        return bytes.toNSData().base64EncodedStringWithOptions(0u)
    }
}


@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData = this.usePinned {
    NSData.create(bytes = it.addressOf(0), length = this.size.toULong())
}