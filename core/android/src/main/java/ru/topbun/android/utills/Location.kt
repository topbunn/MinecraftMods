package ru.topbun.android.utills

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale

enum class LocationAd {
    RU, OTHER
}

fun Context.getLocation(): LocationAd {
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val simCountry = telephonyManager.simCountryIso
    return if (simCountry == "ru") LocationAd.RU else LocationAd.OTHER
}

fun getDeviceLanguage(): String {
    val language = Locale.getDefault().language
    val supportedLanguages = setOf("ru", "de", "es", "fr", "hi", "it", "ja", "ko", "pt", "ar", "en")
    return if (supportedLanguages.contains(language)) language else "en"
}