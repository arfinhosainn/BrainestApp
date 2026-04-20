package com.scellio.brainest.localization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal fun languageIdToTag(languageId: String?): String? {
    return when (languageId) {
        "english" -> "en"
        "arabic" -> "ar"
        "french" -> "fr"
        "spanish" -> "es"
        "german" -> "de"
        "chinese" -> "zh"
        "ukrainian" -> "uk"
        else -> null
    }
}

object AppLanguageState {
    private val _languageTag = MutableStateFlow<String?>(null)
    val languageTag: StateFlow<String?> = _languageTag.asStateFlow()

    fun update(languageTag: String?) {
        _languageTag.value = languageTag
    }
}

expect fun applyAppLanguage(languageTag: String?)
