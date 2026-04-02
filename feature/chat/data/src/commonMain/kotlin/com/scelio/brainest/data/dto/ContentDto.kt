package com.scelio.brainest.data.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class ContentDto {
    @Serializable
    @SerialName("input_text")
    data class Text(
        val text: String
    ) : ContentDto()

    @Serializable
    @SerialName("output_text")
    data class OutputText(
        val text: String
    ) : ContentDto()

    @Serializable
    @SerialName("input_image")
    data class Image(
        @SerialName("image_url")
        val imageUrl: String? = null
    ) : ContentDto()

    @Serializable
    @SerialName("input_file")
    data class File(
        @SerialName("file_id")
        val fileId: String? = null,
        @SerialName("file_url")
        val fileUrl: String? = null
    ) : ContentDto()
}
