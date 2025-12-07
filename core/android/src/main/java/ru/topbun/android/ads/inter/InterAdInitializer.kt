package ru.topbun.android.ads.inter

import android.app.Activity
import android.content.Context
import ru.topbun.android.BuildConfig
import ru.topbun.android.utills.LocationAd
import ru.topbun.domain.entity.ConfigEntity

object InterAdInitializer {

    private var initialized = false
    private var activeNetwork: Network = Network.NONE

    private enum class Network {
        NONE, APPLOVIN, YANDEX
    }

    fun init(context: Context, location: LocationAd, config: ConfigEntity) {
        if (initialized) return
        if (!config.isAdEnabled) return

        initialized = true

        activeNetwork =
            if (!BuildConfig.RUSTORE && location == LocationAd.OTHER) {
                config.applovinInter?.let { ApplovinInterAdManager.init(context, it) }
                Network.APPLOVIN
            } else {
                config.yandexInter?.let { YandexInterAdManager.init(context, it) }
                Network.YANDEX
            }

    }

    fun show(activity: Activity) {
        if (!initialized) return

        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.showInterstitial()
            Network.YANDEX -> YandexInterAdManager.showInterstitial(activity)
            else -> {}
        }
    }

    fun clearCallback() {
        if (!initialized) return

        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.clearCallback()
            Network.YANDEX -> YandexInterAdManager.clearCallback()
            else -> {}
        }
    }

    fun setOnAdReadyCallback(callback: () -> Unit) {
        if (!initialized) return

        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.setOnAdReadyCallback(callback)
            Network.YANDEX -> YandexInterAdManager.setOnAdReadyCallback(callback)
            else -> {}
        }
    }

    fun onStart() {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.resume()
            Network.YANDEX -> YandexInterAdManager.resume()
            else -> {}
        }
    }

    fun onStop() {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.onStop()
            Network.YANDEX -> YandexInterAdManager.onStop()
            else -> {}
        }
    }

    fun onDestroy() {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.destroy()
            Network.YANDEX -> YandexInterAdManager.destroy()
            else -> {}
        }
    }
}
