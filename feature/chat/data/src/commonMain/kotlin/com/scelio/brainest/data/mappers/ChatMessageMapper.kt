package com.scelio.brainest.data.mappers

import com.scelio.brainest.data.dto.ContentDto
import com.scelio.brainest.data.dto.MessageDto
import com.scelio.brainest.domain.models.ChatMessage

fun ChatMessage.toOpenAIMessage(): MessageDto {
    val contentList = mutableListOf<ContentDto>()

    contentList.add(ContentDto.Text(text = content))

    imageUrl?.let {
        contentList.add(ContentDto.Image(imageUrl = it))
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