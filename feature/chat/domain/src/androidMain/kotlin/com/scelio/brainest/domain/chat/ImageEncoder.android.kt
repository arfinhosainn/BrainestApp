package com.scelio.brainest.domain.chat

import android.graphics.Bitmap
import android.util.Base64

actual object ImageEncoder {
    actual suspend fun encodeToBase64(bytes: ByteArray, mimeType: String): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}


fun bitmapToBytes(bitmap: Bitmap): ByteArray {
    val stream = java.io.ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
    return stream.toByteArray()
}