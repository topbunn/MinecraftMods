package ru.topbun.data.repository

import android.content.Context
import android.telephony.TelephonyManager
import ru.topbun.android.utills.LocationAd
import ru.topbun.data.api.LocationApi
import kotlinx.coroutines.withTimeoutOrNull

class LocationRepository(
    private val context: Context,
    private val api: LocationApi,
) {

    suspend fun getLocation(): LocationAd {
        val country = getCountryFromIpWithTimeout()
        return country?.countryCodeToLocationAd() ?: getLocationFromTelephony(context)
    }

    private suspend fun getCountryFromIpWithTimeout() = withTimeoutOrNull(3000) {
        try {
            val response = api.getInfo()
            response.countryCode?.lowercase()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun getLocationFromTelephony(context: Context): LocationAd {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = telephonyManager.simCountryIso?.lowercase()
        val networkCountry = telephonyManager.networkCountryIso?.lowercase()
        val localeCountry = context.resources.configuration.locales[0]?.country?.lowercase()

        val country = when {
            !simCountry.isNullOrBlank() -> simCountry
            !networkCountry.isNullOrBlank() -> networkCountry
            !localeCountry.isNullOrBlank() -> localeCountry
            else -> "other"
        }

        return country.countryCodeToLocationAd()
    }

    private fun String?.countryCodeToLocationAd() =
        if (this == "ru") LocationAd.RU else LocationAd.OTHER
}
