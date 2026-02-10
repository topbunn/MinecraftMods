package ru.topbun.android.ads.open

import android.app.Activity
import ru.topbun.android.BuildConfig
import ru.topbun.android.utills.LocationAd
import ru.topbun.domain.entity.ConfigEntity

object OpenAdInitializer {

    private var initialized = false
    private var activeNetwork: Network = Network.NONE

    private enum class Network {
        NONE, APPLOVIN, YANDEX
    }

    fun init(activity: Activity, location: LocationAd, config: ConfigEntity) {
        if (initialized) return
        if (!config.isOpenAdsEnabled) return

        initialized = true

        activeNetwork =
            if (location == LocationAd.OTHER) {
                config.applovinOpen?.let { ApplovinOpenAdManager.init(activity, it) }
                Network.APPLOVIN
            } else {
                config.yandexOpen?.let { YandexOpenAdManager.init(activity.application, it) }
                Network.YANDEX
            }
    }

    fun show(activity: Activity) {
        if (!initialized) return

        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinOpenAdManager.showIfReady()
            Network.YANDEX -> YandexOpenAdManager.show(activity)
            else -> {}
        }
    }

    fun onStart(activity: Activity) {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> {
                ApplovinOpenAdManager.resume()
                ApplovinOpenAdManager.showIfReady()
            }

            Network.YANDEX -> YandexOpenAdManager.show(activity)
            else -> {}
        }
    }

    fun onStop() {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinOpenAdManager.pause()
            else -> {}
        }
    }

    fun onDestroy() {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinOpenAdManager.destroy()
            Network.YANDEX -> YandexOpenAdManager.destroy()
            else -> {}
        }
        initialized = false
    }


}
