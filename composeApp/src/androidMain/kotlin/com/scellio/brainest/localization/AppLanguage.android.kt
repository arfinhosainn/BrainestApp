package com.scellio.brainest.localization

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

actual fun applyAppLanguage(languageTag: String?) {
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.forLanguageTags(languageTag.orEmpty())
    )
}
