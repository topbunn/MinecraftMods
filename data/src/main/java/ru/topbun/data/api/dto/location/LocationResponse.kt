package ru.topbun.data.api.dto.location

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("country_code")
    val countryCode: String?
)
