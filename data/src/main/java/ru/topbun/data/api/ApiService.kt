package ru.topbun.data.api

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url
import ru.topbun.android.utills.getDeviceLanguage
import ru.topbun.data.BuildConfig
import ru.topbun.data.api.dto.AppInfoDto
import ru.topbun.domain.entity.IssueEntity
import ru.topbun.data.api.dto.GetModsResponse
import ru.topbun.data.api.dto.ModDto
import ru.topbun.domain.entity.mod.ModType

interface ApiService {

    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): ResponseBody

    @POST("/v1/apps/{id}/issue")
    suspend fun createIssue(@Path("id") id: Int, @Body issue: IssueEntity)

    @GET("/v1/apps/{appId}/mod/{status}")
    suspend fun getMods(
        @Path("appId") appId: Int,
        @Path("status") status: String = "actived",
        @Header("language") language: String = getDeviceLanguage(),
        @Query("q") q: String,
        @Query("category") category: ModType?,
        @Query("sort_key") sortKey: String,
        @Query("skip") skip: Int,
        @Query("take") take: Int = 100,
        @Query("sort_value") sortValue: String = "asc",
    ): GetModsResponse

    @GET("/v1/mod/{id}")
    suspend fun getMod(
        @Path("id") id: Int,
        @Header("language") language: String = getDeviceLanguage()
    ): ModDto

    @GET("/v1/apps")
    suspend fun getApps(
        @Header("Lanugage") language: String = getDeviceLanguage()
    ): List<AppInfoDto>

    @GET("/v1/apps/{id}")
    suspend fun loadConfig(
        @Path("id") id: Int,
    ): AppInfoDto

}