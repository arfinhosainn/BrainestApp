package com.scelio.brainest.presentation.model

enum class MessageStatus {
    SENDING,    // Message is being sent
    SENT,       // Message sent successfully
    FAILED,     // Message failed to send
    TYPING      // AI is typing (for assistant messages)
}