package ru.topbun.android.ads.natives

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.topbun.android.ads.natives.NativeAdInitializer.AdMode.Content
import ru.topbun.android.ads.natives.NativeAdInitializer.AdMode.Fullscreen
import ru.topbun.android.ads.natives.applovin.ApplovinContentAdView
import ru.topbun.android.ads.natives.applovin.ApplovinFullscreenAdView
import ru.topbun.android.ads.natives.applovin.ApplovinBannerAdView
import ru.topbun.android.ads.natives.applovin.ApplovinBannerAdPreloader
import ru.topbun.android.ads.natives.applovin.ApplovinNativeAdManager
import ru.topbun.android.ads.natives.yandex.YandexBannerAdView
import ru.topbun.android.ads.natives.yandex.YandexBannerAdPreloader
import ru.topbun.android.ads.natives.yandex.YandexContentAdView
import ru.topbun.android.ads.natives.yandex.YandexFullscreenAdView
import ru.topbun.android.ads.natives.yandex.YandexNativeAdManager
import ru.topbun.android.utills.LocationAd
import ru.topbun.android.utills.shouldShowAd
import ru.topbun.domain.entity.ConfigEntity
import ru.topbun.domain.entity.ConfigEntity.ContentAdType.*

object NativeAdInitializer {

    private sealed interface Network {
        object None: Network
        object ApplovinNative: Network
        data class ApplovinBanner(val adId: String): Network
        object YandexNative: Network
        data class YandexBanner(val adId: String): Network
    }

    enum class AdMode{
        Fullscreen, Content
    }

    enum class PreloadType{
        ERROR, SUCCESS, LOADING
    }

    private var initialized = false
    private var activeNetwork: Network = Network.None
    private var percentShow: Int = 100
    private var nextAdCanShowWithChange = false

    fun init(context: Context, location: LocationAd, config: ConfigEntity) {
        if (initialized) return
        if (!config.isNativeAdsEnabled) return

        initialized = true
        percentShow = config.chanceShowNativeAds
        nextAdCanShowWithChange = shouldShowAd(percentShow)

        activeNetwork =
            if (location == LocationAd.OTHER) {
                when(config.contentAdType){
                    NATIVE -> {
                        config.applovinNative?.let { adUnitId ->
                            ApplovinNativeAdManager.init(adUnitId, config.countNativePreload)
                            ApplovinNativeAdManager.preload()
                        }
                        Network.ApplovinNative
                    }
                    BANNER -> {
                        config.applovinBanner?.let { adUnitId ->
                            ApplovinBannerAdPreloader.init(adUnitId, config.countNativePreload)
                            ApplovinBannerAdPreloader.preload(context)
                        }
                        Network.ApplovinBanner(config.applovinBanner ?: "")
                    }
                }
            } else {
                when(config.contentAdType){
                    NATIVE -> {
                        config.yandexNative?.let { adUnitId ->
                            YandexNativeAdManager.init(context, adUnitId, config.countNativePreload)
                            YandexNativeAdManager.preload()
                        }
                        Network.YandexNative
                    }
                    BANNER -> {
                        config.yandexBanner?.let { adUnitId ->
                            YandexBannerAdPreloader.init(adUnitId, config.countNativePreload)
                            YandexBannerAdPreloader.preload(context)
                        }
                        Network.YandexBanner(config.yandexBanner ?: "")
                    }
                }
            }
    }

    @Composable
    fun show(
        modifier: Modifier = Modifier,
        mode: AdMode = AdMode.Content
    ) {
        if (!initialized) return
        if (!shouldShowAd(percentShow)) return

        when (val network = activeNetwork) {
            Network.ApplovinNative -> {
                when(mode){
                    Fullscreen -> ApplovinFullscreenAdView(modifier = modifier)
                    Content -> ApplovinContentAdView(modifier = modifier)
                }
            }
            is Network.ApplovinBanner -> {
                ApplovinBannerAdView(adUnitId = network.adId, modifier = modifier)
            }
            Network.YandexNative -> {
                when(mode){
                    Fullscreen -> YandexFullscreenAdView(modifier = modifier)
                    Content -> YandexContentAdView(modifier = modifier)
                }
            }
            is Network.YandexBanner -> {
                YandexBannerAdView(network.adId, modifier)
            }
            else -> {}
        }
    }

    fun hasNativeAd(): Boolean =  when {
        !nextAdCanShowWithChange -> false
        !initialized -> false
        activeNetwork == Network.ApplovinNative -> ApplovinNativeAdManager.hasAd()
        activeNetwork == Network.YandexNative -> YandexNativeAdManager.hasAd()
        activeNetwork is Network.ApplovinBanner -> ApplovinBannerAdPreloader.hasAd()
        activeNetwork is Network.YandexBanner -> YandexBannerAdPreloader.hasAd()
        else -> false
    }

    fun isBannerMode(): Boolean = when(activeNetwork){
        is Network.ApplovinBanner, is Network.YandexBanner -> true
        else -> false
    }

    fun setOnListener(callback: (PreloadType) -> Unit) {
        when {
            !initialized -> callback(PreloadType.ERROR)
            else -> when (activeNetwork) {
                Network.ApplovinNative -> ApplovinNativeAdManager.setListener(callback)
                Network.YandexNative -> YandexNativeAdManager.setListener(callback)
                is Network.ApplovinBanner -> ApplovinBannerAdPreloader.setListener(callback)
                is Network.YandexBanner -> YandexBannerAdPreloader.setListener(callback)
                else -> callback(PreloadType.ERROR)
            }
        }
    }

    fun deleteListener() {
        if (!initialized) return
        when (activeNetwork) {
            Network.ApplovinNative -> ApplovinNativeAdManager.deleteListener()
            Network.YandexNative -> YandexNativeAdManager.deleteListener()
            is Network.ApplovinBanner -> ApplovinBannerAdPreloader.deleteListener()
            is Network.YandexBanner -> YandexBannerAdPreloader.deleteListener()
            else -> {}
        }

    }

    fun onDestroy() {
        if (!initialized) return
        when (activeNetwork) {
            Network.ApplovinNative -> ApplovinNativeAdManager.destroy()
            Network.YandexNative -> YandexNativeAdManager.destroy()
            is Network.ApplovinBanner -> ApplovinBannerAdPreloader.destroy()
            is Network.YandexBanner -> YandexBannerAdPreloader.destroy()
            else -> {}
        }
        initialized = false
    }

}