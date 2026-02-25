package com.youlovehamit.app.app

import android.content.Context
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.instream.MobileInstreamAds

fun Context.initYandex() {
    MobileInstreamAds.setAdGroupPreloading(true)
    MobileAds.showDebugPanel(this)
    MobileAds.initialize(this) {}
}