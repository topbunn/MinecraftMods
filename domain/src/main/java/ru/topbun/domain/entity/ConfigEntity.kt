package ru.topbun.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfigEntity(
    val isOpenAdsEnabled: Boolean,
    val isInterAdsEnabled: Boolean,
    val isNativeAdsEnabled: Boolean,

    val applovinOpen: String?,
    val applovinInter: String?,
    val applovinNative: String?,
    val applovinBanner: String?,

    val yandexOpen: String?,
    val yandexInter: String?,
    val yandexNative: String?,
    val yandexBanner: String?,

    val delayInter: Int,
    val contentAdType: ContentAdType,
) : Parcelable{

    enum class ContentAdType{
        NATIVE, BANNER;

        companion object{

            fun fromString(value: String?) = value?.let { ContentAdType.valueOf(it) } ?: NATIVE

        }

    }

}
