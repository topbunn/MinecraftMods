package ru.topbun.android.utills

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.telephony.TelephonyManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

enum class LocationAd {
    RU, OTHER
}

@SuppressLint("MissingPermission")
fun Context.getCountryByCoarseLocation(onResult: (LocationAd) -> Unit) {
    val fused = LocationServices.getFusedLocationProviderClient(this)

    fused.getCurrentLocation(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        CancellationTokenSource().token
    ).addOnSuccessListener { location ->
        if (location == null) {
            onResult(getLocation())
            return@addOnSuccessListener
        }

        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            val countryCode = list?.firstOrNull()?.countryCode?.lowercase()
            val locationAd = if (countryCode == "ru") LocationAd.RU else LocationAd.OTHER
            onResult(locationAd)
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(getLocation())
        }
    }.addOnFailureListener {
        onResult(getLocation())
    }
}


fun Context.getLocation(): LocationAd {
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val simCountry = telephonyManager.simCountryIso?.lowercase()
    val networkCountry = telephonyManager.networkCountryIso?.lowercase()
    val localeCountry = resources.configuration.locales[0].country.lowercase()

    val country = when {
        !simCountry.isNullOrBlank() -> simCountry
        !networkCountry.isNullOrBlank() -> networkCountry
        !localeCountry.isNullOrBlank() -> localeCountry
        else -> "other"
    }

    return if (country == "ru") LocationAd.RU else LocationAd.OTHER
}


fun getDeviceLanguage(): String {
    val language = Locale.getDefault().language
    val supportedLanguages = setOf("ru", "de", "es", "fr", "hi", "it", "ja", "ko", "pt", "ar", "en")
    return if (supportedLanguages.contains(language)) language else "en"
}