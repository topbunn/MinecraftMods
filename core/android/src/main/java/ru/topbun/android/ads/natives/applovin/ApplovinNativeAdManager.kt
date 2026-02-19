package ru.topbun.android.ads.natives.applovin

import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.topbun.android.R
import ru.topbun.android.ads.natives.NativeAdInitializer.PreloadType
import kotlin.math.min
import kotlin.math.pow

object ApplovinNativeAdManager {

    data class NativeAdHolder(
        val ad: MaxAd,
        val view: MaxNativeAdView
    )

    private var poolSize = 1

    private var adLoader: MaxNativeAdLoader? = null
    private val loadedAds = ArrayDeque<MaxAd>(poolSize)

    private var initialized = false
    private var retryAttempt = 0
    private var loadingCount = 0

    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var onPreloadCallback: ((PreloadType) -> Unit)? = null

    fun setListener(callback: (PreloadType) -> Unit) {
        onPreloadCallback = callback
    }

    fun deleteListener() {
        onPreloadCallback = null
    }

    fun hasAd() = loadedAds.isNotEmpty()

    fun init(adUnitId: String, countNativePreload: Int) {
        log { "Инициализация AppLovin Native Ad ($adUnitId)" }
        if (initialized) return
        initialized = true
        poolSize = countNativePreload

        adLoader = MaxNativeAdLoader(adUnitId)
        adLoader?.setNativeAdListener(object : MaxNativeAdListener() {

            override fun onNativeAdLoaded(
                nativeAdView: MaxNativeAdView?,
                nativeAd: MaxAd
            ) {
                retryAttempt = 0
                loadingCount--

                if (loadedAds.size < poolSize) {
                    loadedAds.add(nativeAd)
                    log { "MaxAd добавлен в пул (${loadedAds.size})" }
                } else {
                    adLoader?.destroy(nativeAd)
                    log { "Пул переполнен, MaxAd уничтожен" }
                }

                preloadNext()
                onPreloadCallback?.invoke(
                    if (loadedAds.size >= poolSize) PreloadType.SUCCESS
                    else PreloadType.LOADING
                )
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                retryAttempt++
                loadingCount--

                val delayMs = 2.0.pow(min(retryAttempt, 5)).toLong() * 1000
                log { "Ошибка загрузки AppLovin Native: ${error.message}, retry через $delayMs ms" }

                scope.launch {
                    delay(delayMs)
                    preloadNext()
                }
            }
        })
    }

    private fun preloadNext() {
        if (!initialized) return
        if (loadedAds.size + loadingCount >= poolSize) return

        loadingCount++
        log { "Загрузка следующей AppLovin Native (pool=${loadedAds.size}, loading=$loadingCount)" }

        adLoader?.loadAd()
    }

    fun preload() {
        if (adLoader == null) {
            log { "Реклама еще не инициализирована" }
            return
        }

        log { "Старт предзагрузки AppLovin Native Ads" }
        repeat(poolSize) { preloadNext() }
    }

    fun popAd(context: Context, layoutResId: Int): NativeAdHolder? {
        val ad = loadedAds.removeFirstOrNull() ?: run {
            preloadNext()
            return null
        }

        val binder = MaxNativeAdViewBinder.Builder(layoutResId)
            .setTitleTextViewId(R.id.applovin_title)
            .setBodyTextViewId(R.id.applovin_body)
            .setAdvertiserTextViewId(R.id.applovin_advertiser)
            .setIconImageViewId(R.id.applovin_icon)
            .setMediaContentViewGroupId(R.id.applovin_media)
            .setOptionsContentViewGroupId(R.id.applovin_options)
            .setCallToActionButtonId(R.id.applovin_call_to_action)
            .build()

        val adView = MaxNativeAdView(binder, context.applicationContext)
        adLoader?.render(adView, ad)

        preloadNext()

        return NativeAdHolder(ad, adView)
    }

    fun destroyAd(ad: MaxAd) {
        log { "Реклама очищена" }
        adLoader?.destroy(ad)
    }

    fun destroy() {
        log { "Destroy AppLovin Native Ad Manager" }

        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        loadedAds.forEach { adLoader?.destroy(it) }
        loadedAds.clear()

        adLoader?.setNativeAdListener(null)
        initialized = false
        retryAttempt = 0
        loadingCount = 0
    }

    private fun log(message: () -> String) =
        Log.d("APPLOVIN_NATIVE_AD", message())
}

