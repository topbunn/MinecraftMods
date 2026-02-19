package com.youlovehamit.app.app

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.youlovehamit.app.BuildConfig

fun Application.initAppLovin() {

    val sdk = AppLovinSdk.getInstance(this)
    sdk.showMediationDebugger()

    val settings = sdk.settings

    settings.setVerboseLogging(false)

    settings.termsAndPrivacyPolicyFlowSettings.apply {
        isEnabled = true
        privacyPolicyUri =
            "https://youlovehamit.kz/policy/app-privacy-policy".toUri()

    }

    val initConfig = AppLovinSdkInitializationConfiguration
        .builder(BuildConfig.APPLOVIN_SDK_KEY)
        .setMediationProvider(AppLovinMediationProvider.MAX)
        .build()

    sdk.initialize(initConfig) {
        Log.d("APPLOVIN_INIT", "SDK initialized")
    }
}

