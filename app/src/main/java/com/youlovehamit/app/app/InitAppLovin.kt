package com.youlovehamit.app.app

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.youlovehamit.app.BuildConfig

fun Context.initAppLovin() {
    val sdk = AppLovinSdk.getInstance(this)
    sdk.showMediationDebugger()
    val settings = sdk.settings

    settings.setVerboseLogging(true)

    settings.termsAndPrivacyPolicyFlowSettings.isEnabled = true
    settings.termsAndPrivacyPolicyFlowSettings.privacyPolicyUri =
        "https://youlovehamit.kz/policy/app-privacy-policy".toUri()
    settings.termsAndPrivacyPolicyFlowSettings.debugUserGeography = AppLovinSdkConfiguration.ConsentFlowUserGeography.GDPR

    val initConfig = AppLovinSdkInitializationConfiguration
        .builder(BuildConfig.APPLOVIN_SDK_KEY)
        .setMediationProvider(AppLovinMediationProvider.MAX)
        .build()

    sdk.initialize(initConfig) {
        Log.d("APPLOVIN_INIT", "SDK инициализирован")
    }
}
