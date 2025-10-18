package ru.topbun.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.topbun.data.BuildConfig

object ApiFactory {


    private fun createDefaultOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
        return OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(createDefaultOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create<ApiService>()

}