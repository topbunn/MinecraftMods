package ru.topbun.domain.entity.modConfig

data class ModConfig(
    val applovinSdkKey: String,
    val metricKey: String,
    val applicationId: String,
    val appId: Int,
    val primaryColor: Int,
    val percentShowNativeAd: Int,
    val percentShowInterAd: Int,
)
