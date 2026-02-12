package ru.topbun.android.ads.natives

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.topbun.android.ads.natives.NativeAdInitializer.AdMode.Content
import ru.topbun.android.ads.natives.NativeAdInitializer.AdMode.Fullscreen
import ru.topbun.android.ads.natives.NativeAdInitializer.Network.APPLOVIN
import ru.topbun.android.ads.natives.NativeAdInitializer.Network.NONE
import ru.topbun.android.ads.natives.NativeAdInitializer.Network.YANDEX
import ru.topbun.android.ads.natives.applovin.ApplovinContentAdView
import ru.topbun.android.ads.natives.applovin.ApplovinFullscreenAdView
import ru.topbun.android.ads.natives.applovin.ApplovinNativeAdManager
import ru.topbun.android.ads.natives.yandex.YandexContentAdView
import ru.topbun.android.ads.natives.yandex.YandexFullscreenAdView
import ru.topbun.android.ads.natives.yandex.YandexNativeAdManager
import ru.topbun.android.utills.LocationAd
import ru.topbun.domain.entity.ConfigEntity

object NativeAdInitializer {

    private enum class Network {
        NONE, APPLOVIN, YANDEX
    }

    enum class AdMode{
        Fullscreen, Content
    }

    enum class PreloadType{
        ERROR, SUCCESS, LOADING
    }

    private var initialized = false
    private var activeNetwork: Network = NONE

    fun init(context: Context, location: LocationAd, config: ConfigEntity) {
        if (initialized) return
        if (!config.isNativeAdsEnabled) return

        initialized = true

        activeNetwork =
            if (location == LocationAd.OTHER) {
                config.applovinNative?.let { adUnitId ->
                    ApplovinNativeAdManager.init(context, adUnitId, config.countNativePreload)
                    ApplovinNativeAdManager.preload()
                }
                APPLOVIN
            } else {
                config.yandexNative?.let { adUnitId ->
                    YandexNativeAdManager.init(context, adUnitId, config.countNativePreload)
                    YandexNativeAdManager.preload()
                }
                YANDEX
            }
    }

    @Composable
    fun show(
        modifier: Modifier = Modifier,
        mode: AdMode = AdMode.Content
    ) {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> {
                when(mode){
                    Fullscreen -> ApplovinFullscreenAdView(modifier = modifier)
                    Content -> ApplovinContentAdView(modifier = modifier)
                }

            }
            Network.YANDEX -> {
                when(mode){
                    Fullscreen -> YandexFullscreenAdView(modifier = modifier)
                    Content -> YandexContentAdView(modifier = modifier)
                }
            }
            else -> {}
        }
    }

    fun hasNativeAd(): Boolean =  when {
        !initialized -> false
        activeNetwork == APPLOVIN -> ApplovinNativeAdManager.hasAd()
        activeNetwork == YANDEX -> YandexNativeAdManager.hasAd()
        else -> false
    }

    fun setOnListener(callback: (PreloadType) -> Unit) {
        when {
            !initialized -> callback(PreloadType.ERROR)
            else -> when (activeNetwork) {
                APPLOVIN -> ApplovinNativeAdManager.setListener(callback)
                YANDEX -> YandexNativeAdManager.setListener(callback)
                else -> callback(PreloadType.ERROR)
            }
        }
    }

    fun deleteListener() {
        if (initialized) return
        when (activeNetwork) {
            APPLOVIN -> ApplovinNativeAdManager.deleteListener()
            YANDEX -> YandexNativeAdManager.deleteListener()
            else -> NONE
        }

    }

    fun onDestroy() {
        if (!initialized) return
        when (activeNetwork) {
            Network.APPLOVIN -> ApplovinNativeAdManager.destroy()
            Network.YANDEX -> YandexNativeAdManager.destroy()
            else -> {}
        }
        initialized = false
    }

}