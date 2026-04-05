package com.scelio.brainest.flashcards.domain

import kotlin.time.Instant

enum class StudySourceType(val dbValue: String) {
    DOCUMENT("document"),
    AUDIO("audio");

    companion object {
        fun fromDbValue(value: String): StudySourceType {
            return entries.firstOrNull { it.dbValue == value } ?: DOCUMENT
        }
    }
}

data class StudySource(
    val id: String,
    val deckId: String,
    val sourceType: StudySourceType,
    val sourceText: String?,
    val sourceFileId: String?,
    val sourceFilename: String?,
    val createdAt: Instant
)
