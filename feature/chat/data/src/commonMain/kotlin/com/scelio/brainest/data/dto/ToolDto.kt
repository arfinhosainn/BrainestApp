package com.scelio.brainest.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ToolDto {
    @Serializable
    data class WebSearch(
        val type: String = "web_search"
    ) : ToolDto()

    @Serializable
    data class FileSearch(
        val type: String = "file_search",
        @SerialName("vector_store_ids")
        val vectorStoreIds: List<String>
    ) : ToolDto()
}