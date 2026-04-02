package com.scelio.brainest.presentation.flashcards

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private val SupportedMimeTypes = arrayOf(
    "application/pdf",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "text/plain"
)

@Composable
actual fun rememberDocumentPicker(
    onDocumentPicked: (PickedDocument) -> Unit,
    onError: (String) -> Unit
): DocumentPicker {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val fileName = contentResolver.getDisplayName(uri) ?: "document"
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
        if (bytes == null) {
            onError("Unable to read document.")
        } else {
            onDocumentPicked(
                PickedDocument(
                    fileName = fileName,
                    mimeType = mimeType,
                    bytes = bytes
                )
            )
        }
    }

    return remember {
        object : DocumentPicker {
            override fun launch() {
                launcher.launch(SupportedMimeTypes)
            }
        }
    }
}

private fun ContentResolver.getDisplayName(uri: Uri): String? {
    val cursor = query(uri, null, null, null, null) ?: return null
    cursor.use {
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        return if (cursor.moveToFirst() && nameIndex != -1) {
            cursor.getString(nameIndex)
        } else {
            null
        }
    }
}
