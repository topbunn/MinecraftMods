package com.youlovehamit.app.app

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.youlovehamit.app.BuildConfig

fun initAppLovin(activity: Activity, onComplete: () -> Unit) {
    val sdk = AppLovinSdk.getInstance(activity.applicationContext)
    val settings = sdk.settings
    settings.setVerboseLogging(false)
    val initConfig = AppLovinSdkInitializationConfiguration
        .builder(BuildConfig.APPLOVIN_SDK_KEY)
        .setMediationProvider(AppLovinMediationProvider.MAX)
        .build()

    sdk.initialize(initConfig) {
        Log.d("APPLOVIN_INIT", "SDK initialized")
        showConsentIfNeeded(sdk, activity, onComplete)
    }
}

private fun showConsentIfNeeded(sdk: AppLovinSdk, activity: Activity, onComplete: () -> Unit) {
    val cmpService = sdk.cmpService

    if (!cmpService.hasSupportedCmp()) {
        Log.d("CMP", "CMP не требуется")
        onComplete()
        return
    }

    cmpService.showCmpForExistingUser(activity) { error ->
        if (error != null) {
            Log.e("CMP", "CMP error: ${error.message}")
        } else {
            Log.d("CMP", "CMP completed or not required")
        }
        onComplete()
    }
}
