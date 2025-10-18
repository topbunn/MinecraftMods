package ru.topbun.data.api.dto

import com.google.gson.annotations.SerializedName
import ru.topbun.data.BuildConfig
import ru.topbun.domain.entity.app.AppInfoEntity
import ru.topbun.domain.entity.app.AppInfoStatusType

data class AppInfoDto(
    val order: Int,
    val packageName: String,
    val logo: String,
    val status: AppInfoStatusType,
    val sdk: SdkInfoDto? = null,
    val translations: List<AppTranslationDto>
)

fun List<AppInfoDto>.toEntity(applicationId: String) =
    filter { it.status == AppInfoStatusType.PUBLISHED && it.packageName != applicationId }
        .sortedBy { it.order }
        .map {
            AppInfoEntity(
                googlePlayLink = "https://play.google.com/store/apps/details?id=" + it.packageName,
                logoLink = BuildConfig.BASE_URL + it.logo,
                name = it.translations.firstOrNull()?.name ?: ""
            )
        }

data class AppTranslationDto(
    val name: String
)

data class SdkInfoDto(
    @SerializedName("isAdsEnabled") val isAdEnabled: Boolean,
    @SerializedName("secondOpenCode") val applovinOpen: String,
    @SerializedName("secondInterCode") val applovinInter: String,
    @SerializedName("secondNativeCode") val applovinNative: String,
    @SerializedName("thirdOpenCode") val yandexOpen: String,
    @SerializedName("thirdInterCode") val yandexInter: String,
    @SerializedName("thirdNativeCode") val yandexNative: String,
)
