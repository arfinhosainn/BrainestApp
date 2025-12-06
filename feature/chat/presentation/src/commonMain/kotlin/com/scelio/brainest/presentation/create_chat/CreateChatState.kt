package com.scelio.brainest.presentation.create_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.scelio.brainest.presentation.util.UiText

data class CreateChatState(
    val queryTextState: TextFieldState = TextFieldState(),
    val isAddingParticipant: Boolean = false,
    val isLoadingParticipants: Boolean = false,
    val canAddParticipant: Boolean = false,
    val searchError: UiText? = null,
    val isCreatingChat: Boolean = false,
)