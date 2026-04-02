package com.scelio.brainest.data.mappers

import com.scelio.brainest.data.dto.ContentDto
import com.scelio.brainest.data.dto.MessageDto
import com.scelio.brainest.data.dto.MessageRoles
import com.scelio.brainest.domain.models.ChatMessage

fun ChatMessage.toOpenAIMessage(): MessageDto {
    val contentList = mutableListOf<ContentDto>()

    val textContent = if (role == MessageRoles.ASSISTANT) {
        ContentDto.OutputText(text = content)
    } else {
        ContentDto.Text(text = content)
    }
    contentList.add(textContent)

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
