package ru.topbun.android.ads.inter

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.*
import ru.topbun.android.utills.LocationAd
import ru.topbun.domain.entity.ConfigEntity

object InterAdInitializer {

    private var initialized = false
    private var activeNetwork: Network = Network.NONE

    private enum class Network {
        NONE, APPLOVIN, YANDEX
    }

    private var lastShowTime = 0L
    private var delaySeconds = 90

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var reloadJob: Job? = null

    fun init(context: Context, location: LocationAd, config: ConfigEntity) {
        if (initialized || !config.isInterAdsEnabled) return

        initialized = true
        delaySeconds = maxOf(90, config.delayInter)

        activeNetwork =
            if (location == LocationAd.OTHER) {
                config.applovinInter?.let {
                    ApplovinInterAdManager.init(context, it)
                }

                ApplovinInterAdManager.setOnAdShownAction {
                    markShown()
                }

                ApplovinInterAdManager.setOnAdClosedAction {
                    scheduleNextLoad()
                }

                Network.APPLOVIN
            } else {
                config.yandexInter?.let {
                    YandexInterAdManager.init(context, it)
                }
                Network.YANDEX
            }

        scheduleNextLoad(immediate = true)
    }

    fun show(activity: Activity) {
        if (!initialized || !canShow()) return

        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.show(activity)
            Network.YANDEX -> YandexInterAdManager.show(activity)
            else -> {}
        }
    }

    fun isReadyToShow(): Boolean {
        if (!initialized) return false

        val cooldownPassed =
            System.currentTimeMillis() - lastShowTime >= delaySeconds * 1000L

        if (!cooldownPassed) return false

        return when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.isAdReady()
            Network.YANDEX -> YandexInterAdManager.isAdReady()
            else -> false
        }
    }

    private fun canShow(): Boolean {
        val now = System.currentTimeMillis()
        return now - lastShowTime >= delaySeconds * 1000L
    }

    private fun markShown() {
        lastShowTime = System.currentTimeMillis()
    }

    private fun scheduleNextLoad(immediate: Boolean = false) {
        reloadJob?.cancel()

        reloadJob = scope.launch {
            if (!immediate) {
                delay(delaySeconds * 1000L)
            }

            when (activeNetwork) {
                Network.APPLOVIN -> ApplovinInterAdManager.load()
                Network.YANDEX -> YandexInterAdManager.load()
                else -> {}
            }
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
            Network.APPLOVIN -> ApplovinInterAdManager.pause()
            Network.YANDEX -> YandexInterAdManager.pause()
            else -> {}
        }
    }

    fun onDestroy() {
        reloadJob?.cancel()
        scope.cancel()

        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinInterAdManager.destroy()
            Network.YANDEX -> YandexInterAdManager.destroy()
            else -> {}
        }

        initialized = false
        lastShowTime = 0L
    }
}
