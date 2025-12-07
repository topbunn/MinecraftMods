package ru.topbun.android.utills

import java.util.Locale


fun getDeviceLanguage(): String {
    val language = Locale.getDefault().language
    val supportedLanguages = setOf("ru", "de", "es", "fr", "hi", "it", "ja", "ko", "pt", "ar", "en")
    return if (supportedLanguages.contains(language)) language else "en"
}