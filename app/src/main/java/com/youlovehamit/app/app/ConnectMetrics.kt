package com.youlovehamit.app.app

import android.content.Context
import com.youlovehamit.app.BuildConfig
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

fun Context.connectMetrics(){
    val config = AppMetricaConfig
        .newConfigBuilder(BuildConfig.METRIC_KEY)
        .withCrashReporting(true)
        .build()
    AppMetrica.activate(this, config)
}