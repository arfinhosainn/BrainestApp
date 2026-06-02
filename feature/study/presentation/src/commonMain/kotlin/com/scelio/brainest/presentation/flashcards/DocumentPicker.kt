package com.scelio.brainest.presentation.flashcards

import androidx.compose.runtime.Composable

data class PickedDocument(
    val fileName: String,
    val mimeType: String,
    val bytes: ByteArray
)

interface DocumentPicker {
    fun launch()
    fun launchAudio()
}

@Composable
expect fun rememberDocumentPicker(
    onDocumentPicked: (PickedDocument) -> Unit,
    onError: (String) -> Unit = {}
): DocumentPicker
