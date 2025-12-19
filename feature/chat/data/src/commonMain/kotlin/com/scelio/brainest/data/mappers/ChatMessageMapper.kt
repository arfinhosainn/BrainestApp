package com.scelio.brainest.data.mappers

import com.scelio.brainest.data.dto.ContentDto
import com.scelio.brainest.data.dto.MessageDto
import com.scelio.brainest.domain.models.ChatMessage

fun ChatMessage.toOpenAIMessage(): MessageDto {
    val contentList = mutableListOf<ContentDto>()

    contentList.add(ContentDto.Text(text = content))

    imageUrls?.forEach { url ->
        contentList.add(ContentDto.Image(imageUrl = url))
    }

    if (imageUrls == null && imageUrl != null) {
        contentList.add(ContentDto.Image(imageUrl = imageUrl))
    }

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