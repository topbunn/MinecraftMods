package ru.topbun.android.ads.natives.yandex

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdEventListener
import com.yandex.mobile.ads.nativeads.NativeAdException
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration
import com.yandex.mobile.ads.nativeads.NativeAdView
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder
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

object YandexNativeAdManager {

    private var poolSize = 1

    private var adLoader: NativeAdLoader? = null
    private val loadedAdViews = ArrayDeque<NativeAd>(poolSize)
    private var adUnitId: String? = null

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

    fun hasAd() = loadedAdViews.isNotEmpty()

    fun init(context: Context, adUnitId: String, countNativePreload: Int) {
        log { "Инициализация Yandex Native Ad с adUnitId=$adUnitId" }
        if (initialized) return
        poolSize = countNativePreload
        YandexNativeAdManager.adUnitId = adUnitId
        initialized = true

        adLoader = NativeAdLoader(context).apply {
            setNativeAdLoadListener(object : NativeAdLoadListener {
                override fun onAdLoaded(nativeAd: NativeAd) {
                    retryAttempt = 0
                    loadingCount--

                    if (loadedAdViews.size < poolSize) {
                        loadedAdViews.add(nativeAd)
                        log { "NativeAd добавлен в пул (${loadedAdViews.size})" }
                    } else {
                        log { "Пул переполнен, NativeAd пропущена" }
                    }

                    preloadNext()
                    onPreloadCallback?.invoke(
                        if (loadedAdViews.size >= poolSize) PreloadType.SUCCESS
                        else PreloadType.LOADING
                    )
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    retryAttempt++
                    loadingCount--
                    val delayMs = 2.0.pow(min(retryAttempt, 5)).toLong() * 1000
                    log { "Ошибка загрузки Yandex Native Ad: ${error.description}. Повтор через $delayMs ms" }

                    scope.launch {
                        delay(delayMs)
                        preloadNext()
                    }
                }
            })
        }
    }

    private fun bindAdToView(ad: NativeAd, adView: NativeAdView) {
        val binder = NativeAdViewBinder.Builder(adView)
            .setTitleView(adView.findViewById(R.id.ad_yandex_title))
            .setDomainView(adView.findViewById(R.id.ad_yandex_domain))
            .setWarningView(adView.findViewById(R.id.ad_yandex_warning))
            .setSponsoredView(adView.findViewById(R.id.ad_yandex_sponsored))
            .setFeedbackView(adView.findViewById(R.id.ad_yandex_feedback))
            .setCallToActionView(adView.findViewById(R.id.ad_yandex_cta))
            .setMediaView(adView.findViewById(R.id.ad_yandex_media))
            .setIconView(adView.findViewById(R.id.ad_yandex_icon))
            .setPriceView(adView.findViewById(R.id.ad_yandex_price))
            .setBodyView(adView.findViewById(R.id.ad_yandex_body))
            .build()

        try {
            ad.bindNativeAd(binder)
            ad.setNativeAdEventListener(object : NativeAdEventListener {
                override fun onAdClicked() {
                    log { "Yandex Native Ad clicked" }
                }

                override fun onLeftApplication() {}
                override fun onReturnedToApplication() {}
                override fun onImpression(impressionData: ImpressionData?) {
                    log { "Yandex Native Ad impression" }
                }
            })
        } catch (_: NativeAdException) {
        }
    }

    private fun preloadNext() {
        if (!initialized) return
        if (loadedAdViews.size + loadingCount >= poolSize) return

        loadingCount++
        log { "Запуск загрузки следующей Yandex Native Ad (текущий пул: ${loadedAdViews.size}, в процессе: $loadingCount)" }

        adUnitId?.let {
            val request = NativeAdRequestConfiguration.Builder(it)
                .build()

            adLoader?.loadAd(request)
        } ?: run {
            log { "adUnitId == null" }
            return
        }

    }

    fun preload() {
        if (!initialized) {
            log { "Реклама еще не инициализирована" }
            return
        }

        log { "Старт предзагрузки Yandex Native Ads" }
        repeat(poolSize) { preloadNext() }
    }

    fun popAd(context: Context, layoutResId: Int): NativeAdView? {
        val ad = loadedAdViews.removeFirstOrNull() ?: run {
            log { "PopAd. Реклама не загружена" }
            preloadNext()
            return null
        }

        log { "NativeAd выдан из пула (осталось ${loadedAdViews.size})" }

        val adView = LayoutInflater.from(context)
            .inflate(layoutResId, null) as NativeAdView

        bindAdToView(ad, adView)
        preloadNext()

        return adView
    }

    fun destroy() {
        log { "Destroy Yandex Native Ad Manager" }

        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        adLoader = null
        initialized = false
        retryAttempt = 0
        loadingCount = 0
    }

    private fun log(message: () -> String) = Log.d("YANDEX_NATIVE_AD", message())
}
