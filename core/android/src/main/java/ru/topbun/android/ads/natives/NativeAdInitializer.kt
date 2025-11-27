package ru.topbun.android.ads.natives

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.topbun.android.BuildConfig
import ru.topbun.android.ads.natives.NativeAdInitializer.Network.APPLOVIN
import ru.topbun.android.ads.natives.NativeAdInitializer.Network.NONE
import ru.topbun.android.ads.natives.NativeAdInitializer.Network.YANDEX
import ru.topbun.android.utills.LocationAd
import ru.topbun.android.utills.getLocation
import ru.topbun.domain.entity.ConfigEntity

object NativeAdInitializer {

    private var initialized = false
    private var activeNetwork: Network = NONE

    private enum class Network {
        NONE, APPLOVIN, YANDEX
    }

    fun init(context: Context, config: ConfigEntity) {
        if (initialized) return
        if (!config.isAdEnabled) return

        initialized = true

        val location = context.getLocation()

        activeNetwork =
            if (!BuildConfig.RUSTORE && location == LocationAd.OTHER) {
                config.applovinNative?.let {
                    ApplovinNativeAdManager.init(context, it)
                    ApplovinNativeAdManager.preload(context)
                }
                APPLOVIN
            } else {
                config.yandexNative?.let {
                    YandexNativeAdManager.init(context, it)
                    YandexNativeAdManager.preload()
                }
                YANDEX
            }

    }

    @Composable
    fun show(context: Context, modifier: Modifier = Modifier) {
        if (!initialized) return
        when (activeNetwork) {
            APPLOVIN -> ApplovinNativeAdView(context = context, modifier = modifier)
            YANDEX -> YandexNativeAdView(modifier)
            else -> {}
        }
    }

    fun onDestroy() {
        if (!initialized) return
        when (activeNetwork) {
            APPLOVIN -> ApplovinNativeAdManager.destroy()
            YANDEX -> YandexNativeAdManager.destroy()
            else -> {}
        }
    }
}