package com.youlovehamit.app

import ru.topbun.domain.entity.modConfig.ModConfig
import ru.topbun.domain.entity.modConfig.ModConfigProvider

class AppModConfigProvider : ModConfigProvider {
    override fun getConfig(): ModConfig = ModConfig(
        applovinSdkKey = BuildConfig.APPLOVIN_SDK_KEY,
        metricKey = BuildConfig.METRIC_KEY,
        applicationId = BuildConfig.APPLICATION_ID,
        appId = BuildConfig.APP_ID,
        primaryColor = BuildConfig.PRIMARY_COLOR,
    )
}