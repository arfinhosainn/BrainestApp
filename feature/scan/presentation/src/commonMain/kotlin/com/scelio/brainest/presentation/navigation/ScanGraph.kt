package com.scelio.brainest.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.chat.ChatRepository
import com.scelio.brainest.domain.chat.ImageEncoder
import com.scelio.brainest.domain.models.CreateChatRequest
import com.scelio.brainest.domain.models.SendMessageRequest
import com.scelio.brainest.domain.models.SendMessageStreamEvent
import com.scelio.brainest.presentation.scan.CameraScreen
import com.scelio.brainest.presentation.scan.readLocalImageBytes
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private const val ScanSystemPrompt =
    "When the user sends an image without text, analyze the image and provide a concise, helpful explanation of what it contains."

sealed interface ScanGraphRoutes {
    @Serializable
    data object Graph : ScanGraphRoutes
    @Serializable
    data object Scan : ScanGraphRoutes

    companion object {
        const val SCAN_ROUTE = "scan"
    }
}

fun NavGraphBuilder.scanGraph(
    navController: NavHostController,
    authService: AuthService,
    chatRepository: ChatRepository,
    onScanCompletedNavigateToChat: (String) -> Unit
) {
    composable<ScanGraphRoutes.Scan> { _ ->
        val scope = rememberCoroutineScope()
        var isProcessing by remember { mutableStateOf(false) }

        CameraScreen(
            isProcessing = isProcessing,
            onImageCaptured = { imagePath ->
                if (isProcessing) return@CameraScreen
                isProcessing = true

                scope.launch {
                    try {
                        val userId: String = authService.currentUserId() ?: return@launch
                        val imageBytes: ByteArray = readLocalImageBytes(imagePath) ?: return@launch
                        val imageBase64: String = ImageEncoder.encodeToBase64(
                            bytes = imageBytes,
                            mimeType = "image/jpeg"
                        )

                        val chat = chatRepository.createChat(
                            CreateChatRequest(
                                userId = userId,
                                title = "Scanned Image",
                                systemPrompt = ScanSystemPrompt
                            )
                        )

                        val request = SendMessageRequest(
                            chatId = chat.id,
                            userId = userId,
                            content = "",
                            imageUrl = "data:image/jpeg;base64,$imageBase64"
                        )

                        var hasCompletedResponse = false
                        chatRepository.sendMessageStream(request).collect { event: SendMessageStreamEvent ->
                            if (event is SendMessageStreamEvent.Completed) {
                                hasCompletedResponse = true
                            }
                        }

                        if (hasCompletedResponse) {
                            onScanCompletedNavigateToChat(chat.id)
                        }
                    } catch (_: Throwable) {
                        // Keep user on scan screen on any processing error.
                    } finally {
                        isProcessing = false
                    }
                }
            },
            onCameraReady = { /* Handle camera ready */ },
            onCloseRequested = {
                navController.popBackStack()
            }
        )
    }
}
