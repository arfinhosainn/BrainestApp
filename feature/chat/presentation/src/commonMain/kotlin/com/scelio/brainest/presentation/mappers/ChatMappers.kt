package com.scelio.brainest.presentation.mappers

import com.scelio.brainest.domain.models.Chat
import com.scelio.brainest.domain.models.ChatMessage
import com.scelio.brainest.domain.models.ChatWithLastMessage
import com.scelio.brainest.domain.models.MessageMetadata
import com.scelio.brainest.presentation.model.ChatItemUi
import com.scelio.brainest.presentation.model.ChatMessageUi
import com.scelio.brainest.presentation.model.MessageMetadataUi
import com.scelio.brainest.presentation.util.DateUtils

fun Chat.toUi(
    lastMessage: ChatMessage? = null
): ChatItemUi {
    return ChatItemUi(
        id = id,
        title = title,
        lastMessage = lastMessage?.content,
        timestamp = DateUtils.formatMessageTime(lastActivityAt),
        model = model
    )
}

fun ChatWithLastMessage.toUi(): ChatItemUi {
    return ChatItemUi(
        id = chat.id,
        title = chat.title,
        lastMessage = lastMessage?.content,
        timestamp = DateUtils.formatMessageTime(chat.lastActivityAt),
        model = chat.model
    )
}

fun ChatMessage.toUi(): ChatMessageUi {
    return ChatMessageUi(
        id = id,
        content = content,
        isFromUser = role == "user",
        timestamp = DateUtils.formatMessageTime(createdAt),
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