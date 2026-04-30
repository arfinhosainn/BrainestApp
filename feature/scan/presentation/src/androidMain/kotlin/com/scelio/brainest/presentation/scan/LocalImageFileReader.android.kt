package com.scelio.brainest.presentation.scan

import java.io.File

actual fun readLocalImageBytes(imagePath: String): ByteArray? {
    return runCatching { File(imagePath).takeIf { it.exists() }?.readBytes() }.getOrNull()
}
