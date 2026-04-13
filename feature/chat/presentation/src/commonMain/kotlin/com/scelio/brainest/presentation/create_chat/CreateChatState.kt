package com.scelio.brainest.presentation.create_chat

import androidx.compose.runtime.Stable
import com.scelio.brainest.presentation.util.UiText

@Stable
data class CreateChatState(
    val queryText: String = "",
    val isAddingParticipant: Boolean = false,
    val isLoadingParticipants: Boolean = false,
    val canAddParticipant: Boolean = false,
    val searchError: UiText? = null,
    val isCreatingChat: Boolean = false,
)