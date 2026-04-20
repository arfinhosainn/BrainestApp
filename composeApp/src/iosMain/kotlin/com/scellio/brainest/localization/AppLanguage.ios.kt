package com.scellio.brainest.localization

import platform.Foundation.NSUserDefaults

actual fun applyAppLanguage(languageTag: String?) {
    val defaults = NSUserDefaults.standardUserDefaults
    if (languageTag.isNullOrBlank()) {
        defaults.removeObjectForKey("AppleLanguages")
    } else {
        defaults.setObject(listOf(languageTag), forKey = "AppleLanguages")
    }
    defaults.synchronize()
}
