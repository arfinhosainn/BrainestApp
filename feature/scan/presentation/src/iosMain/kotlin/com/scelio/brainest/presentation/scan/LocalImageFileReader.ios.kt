@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.scelio.brainest.presentation.scan

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.posix.memcpy

actual fun readLocalImageBytes(imagePath: String): ByteArray? {
    val data = NSFileManager.defaultManager.contentsAtPath(imagePath) ?: return null
    return data.toByteArray()
}

private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)
    val byteArray = ByteArray(length)
    byteArray.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length.toULong())
    }
    return byteArray
}
