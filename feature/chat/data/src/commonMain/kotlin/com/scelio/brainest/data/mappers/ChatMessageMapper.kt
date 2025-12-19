package com.scelio.brainest.data.mappers

import com.scelio.brainest.data.dto.ContentDto
import com.scelio.brainest.data.dto.MessageDto
import com.scelio.brainest.domain.models.ChatMessage

fun ChatMessage.toOpenAIMessage(): MessageDto {
    val contentList = mutableListOf<ContentDto>()

    // Always add text content
    contentList.add(ContentDto.Text(text = content))

    // Handle multiple images (new approach)
    imageUrls?.forEach { url ->
        contentList.add(ContentDto.Image(imageUrl = url))
    }

    // Handle single image (backward compatibility)
    if (imageUrls == null && imageUrl != null) {
        contentList.add(ContentDto.Image(imageUrl = imageUrl))
    }

    // Handle file
    fileId?.let {
        contentList.add(ContentDto.File(fileId = it))
    }

    return MessageDto(
        role = role,
        content = contentList
    )
}

fun List<ChatMessage>.toOpenAIMessages(): List<MessageDto> {
    return map { it.toOpenAIMessage() }
}