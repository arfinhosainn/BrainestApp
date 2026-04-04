package com.scelio.brainest.presentation.flashcards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.posix.memcpy

private val SupportedDocumentTypes = listOf(
    "com.adobe.pdf",
    "org.openxmlformats.wordprocessingml.document",
    "public.plain-text"
)

@Composable
actual fun rememberDocumentPicker(
    onDocumentPicked: (PickedDocument) -> Unit,
    onError: (String) -> Unit
): DocumentPicker {
    val delegate = remember { DocumentPickerDelegate(onDocumentPicked, onError) }
    return remember { IosDocumentPicker(delegate) }
}

private class IosDocumentPicker(
    private val delegate: DocumentPickerDelegate
) : DocumentPicker {
    override fun launch() {
        val picker = UIDocumentPickerViewController(
            documentTypes = SupportedDocumentTypes,
            inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
        )
        picker.allowsMultipleSelection = false
        picker.delegate = delegate

        val windows = UIApplication.sharedApplication.windows
            .filterIsInstance<UIWindow>()
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: windows.firstOrNull { it.isKeyWindow() }?.rootViewController
            ?: windows.firstOrNull()?.rootViewController

        if (rootViewController == null) {
            delegate.notifyError("Unable to open document picker.")
            return
        }

        rootViewController.presentViewController(
            picker,
            animated = true,
            completion = null
        )
    }
}

private class DocumentPickerDelegate(
    private val onDocumentPicked: (PickedDocument) -> Unit,
    private val onError: (String) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {

    fun notifyError(message: String) = onError(message)

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        if (url == null) {
            onError("No document selected.")
            return
        }

        val startedAccessing = url.startAccessingSecurityScopedResource()
        val path = url.relativePath
        val data = path?.let { NSFileManager.defaultManager.contentsAtPath(it) }
        if (startedAccessing) {
            url.stopAccessingSecurityScopedResource()
        }

        if (data == null) {
            onError("Unable to read document.")
            return
        }

        val fileName = url.lastPathComponent ?: "document"
        val mimeType = mimeTypeFromFileName(fileName)
        val bytes = data.toByteArray()

        onDocumentPicked(
            PickedDocument(
                fileName = fileName,
                mimeType = mimeType,
                bytes = bytes
            )
        )
    }
}

private fun mimeTypeFromFileName(fileName: String): String {
    val lower = fileName.lowercase()
    return when {
        lower.endsWith(".pdf") -> "application/pdf"
        lower.endsWith(".docx") -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        lower.endsWith(".txt") -> "text/plain"
        else -> "application/octet-stream"
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return ByteArray(0)
    val byteArray = ByteArray(length)
    byteArray.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length.toULong())
    }
    return byteArray
}
