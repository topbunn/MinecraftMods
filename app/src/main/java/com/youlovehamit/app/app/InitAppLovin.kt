package com.youlovehamit.app.app

import android.content.Context
import android.net.Uri
import android.util.Log
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.youlovehamit.app.BuildConfig

fun Context.initAppLovin() {
    val sdk = AppLovinSdk.getInstance(this)
    sdk.showMediationDebugger()
    val settings = sdk.settings
    settings.setVerboseLogging(true)
    settings.termsAndPrivacyPolicyFlowSettings.isEnabled = true
    settings.termsAndPrivacyPolicyFlowSettings.privacyPolicyUri = Uri.parse("https://youlovehamit.kz/policy/app-privacy-policy")
    settings.termsAndPrivacyPolicyFlowSettings.setShowTermsAndPrivacyPolicyAlertInGdpr(true)
    val initConfig = AppLovinSdkInitializationConfiguration
        .builder(BuildConfig.APPLOVIN_SDK_KEY)
        .setMediationProvider(AppLovinMediationProvider.MAX)
        .build()
    sdk.initialize(initConfig) {
        Log.d("APPLOVIN_INIT", "SDK инициализирован")
    }
}