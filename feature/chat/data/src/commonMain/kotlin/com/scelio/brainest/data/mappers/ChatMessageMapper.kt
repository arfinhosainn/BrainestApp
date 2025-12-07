package com.scelio.brainest.data.mappers

import com.scelio.brainest.data.dto.ContentDto
import com.scelio.brainest.data.dto.MessageDto
import com.scelio.brainest.domain.models.ChatMessage

fun ChatMessage.toOpenAIMessage(): MessageDto {
    val contentList = mutableListOf<ContentDto>()

    // Add text content
    contentList.add(ContentDto.Text(text = content))

    // Add image if present
    imageUrl?.let {
        contentList.add(ContentDto.Image(imageUrl = it))
    }

    // Add file if present
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