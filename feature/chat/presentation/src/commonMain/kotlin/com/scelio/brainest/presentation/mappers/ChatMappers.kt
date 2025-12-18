package com.scelio.brainest.presentation.mappers

import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.models.ChatWithLastMessage
import com.scelio.brainest.domain.models.MessageMetadata
import com.scelio.brainest.presentation.model.ChatItemUi
import com.scelio.brainest.presentation.model.ChatMessageUi
import com.scelio.brainest.presentation.model.MessageMetadataUi
import com.scelio.brainest.presentation.util.DateTimeFormatter
import com.scelio.brainest.presentation.util.UiText

fun Chat.toUi(
    lastMessage: ChatMessage? = null
): ChatItemUi {
    return ChatItemUi(
        id = id,
        title = title,
        lastMessage = lastMessage?.content,
        timestamp = lastActivityAt, // Pass Instant directly
        model = model
    )
}

fun ChatWithLastMessage.toUi(): ChatItemUi {
    return ChatItemUi(
        id = chat.id,
        title = chat.title,
        lastMessage = lastMessage?.content,
        timestamp = chat.lastActivityAt, // Pass Instant directly
        model = chat.model
    )
}

fun ChatMessage.toUi(): ChatMessageUi {
    return ChatMessageUi(
        id = id,
        content = content,
        isFromUser = role == "user",
        timestamp = UiText.DynamicString(
            DateTimeFormatter.formatTime(createdAt)
        ),
        imageUrl = imageUrl,
        metadata = metadata?.toUi()
    )
}
fun MessageMetadata.toUi(): MessageMetadataUi {
    return MessageMetadataUi(
        model = model,
        tokensUsed = tokensUsed
    )
}