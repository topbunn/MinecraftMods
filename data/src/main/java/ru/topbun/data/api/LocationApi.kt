package ru.topbun.data.api

import retrofit2.http.GET
import ru.topbun.data.api.dto.location.LocationResponse

interface LocationApi {

    @GET("https://ipapi.co/json")
    suspend fun getInfo(): LocationResponse

}