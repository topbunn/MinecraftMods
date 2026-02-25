package ru.topbun.android.ads.inter

import android.app.Activity
import android.app.Application
import kotlinx.coroutines.*
import ru.topbun.android.utills.LocationAd
import ru.topbun.android.utills.shouldShowAd
import ru.topbun.domain.entity.ConfigEntity

object InterAdInitializer {

    private var initialized = false
    private var activeNetwork: Network = Network.NONE

    private var percentShow: Int = 100
    private var nextAdCanShowWithChance = false

    private var lastShowTime = 0L
    private var delaySeconds = 90

    private enum class Network {
        NONE, APPLOVIN, YANDEX
    }

    fun init(
        application: Application,
        location: LocationAd,
        config: ConfigEntity
    ) {
        if (initialized || !config.isInterAdsEnabled) return

        initialized = true

        delaySeconds = config.delayInter
        percentShow = config.chanceShowInterAds
        nextAdCanShowWithChance = shouldShowAd(percentShow)

        activeNetwork =
            if (location == LocationAd.OTHER) {
                config.applovinInter?.let {
                    ApplovinInterAdManager.init(it, delaySeconds)
                }
                Network.APPLOVIN
            } else {
                config.yandexInter?.let {
                    YandexInterAdManager.init(application, it, delaySeconds)
                }
                Network.YANDEX
            }
    }

    fun show(activity: Activity) {
        if (!initialized) return
        if (!canShow()) return
        if (!nextAdCanShowWithChance) return

        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.show(activity)
            Network.YANDEX -> YandexInterAdManager.show(activity)
            else -> return
        }

        markShown()
        nextAdCanShowWithChance = shouldShowAd(percentShow)
    }

    fun isReadyToShow(): Boolean {
        if (!initialized) return false
        if (!canShow()) return false
        if (!nextAdCanShowWithChance) return false

        return when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.isAdReady()
            Network.YANDEX -> YandexInterAdManager.isAdReady()
            else -> false
        }
    }

    private fun canShow(): Boolean {
        return System.currentTimeMillis() - lastShowTime >= delaySeconds * 1000L
    }

    private fun markShown() {
        lastShowTime = System.currentTimeMillis()
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
            Network.APPLOVIN -> ApplovinInterAdManager.pause()
            Network.YANDEX -> YandexInterAdManager.pause()
            else -> {}
        }
    }

    fun onDestroy() {
        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.destroy()
            Network.YANDEX -> YandexInterAdManager.destroy()
            else -> {}
        }

        initialized = false
    }
}

