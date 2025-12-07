package ru.topbun.android.ads.natives

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.mp.KoinPlatform.getKoin
import ru.topbun.android.BuildConfig
import ru.topbun.android.ads.natives.NativeAdInitializer.Network.APPLOVIN
import ru.topbun.android.ads.natives.NativeAdInitializer.Network.NONE
import ru.topbun.android.ads.natives.NativeAdInitializer.Network.YANDEX
import ru.topbun.android.utills.LocationAd
import ru.topbun.domain.entity.ConfigEntity
import ru.topbun.domain.entity.modConfig.ModConfigProvider

object NativeAdInitializer {

    val configProvider: ModConfigProvider get() = getKoin().get()

    private var initialized = false
    private var activeNetwork: Network = NONE

    private enum class Network {
        NONE, APPLOVIN, YANDEX
    }

    fun init(context: Context, location: LocationAd, config: ConfigEntity) {
        if (initialized) return
        if (!config.isAdEnabled) return

        initialized = true

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
    fun show(modifier: Modifier = Modifier) {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinNativeAdView(modifier = modifier)
            Network.YANDEX -> YandexNativeAdView(modifier = modifier)
            else -> {}
        }
    }

    fun onDestroy() {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinNativeAdManager.destroy()
            Network.YANDEX -> YandexNativeAdManager.destroy()
            else -> {}
        }
    }
}