package com.scelio.brainest.data.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("content_type")  // <-- Add this to avoid conflict
sealed class ContentDto {
    @Serializable
    @SerialName("input_text")
    data class Text(
        val type: String = "input_text",
        val text: String
    ) : ContentDto()

    @Serializable
    @SerialName("input_image")
    data class Image(
        val type: String = "input_image",
        @SerialName("image_url")
        val imageUrl: String? = null
    ) : ContentDto()

    @Serializable
    @SerialName("input_file")
    data class File(
        val type: String = "input_file",
        @SerialName("file_id")
        val fileId: String? = null,
        @SerialName("file_url")
        val fileUrl: String? = null
    ) : ContentDto()
}