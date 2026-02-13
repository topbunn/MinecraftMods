package ru.topbun.android.ads.natives.yandex

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.topbun.android.ads.natives.NativeAdInitializer.PreloadType
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

object YandexBannerAdPreloader {

    private var poolSize = 1
    private val loadedViews = ArrayDeque<BannerAdView>(poolSize)
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

    fun hasAd() = loadedViews.isNotEmpty()

    fun init(adUnitId: String, poolSize: Int) {
        if (initialized) return
        this.adUnitId = adUnitId
        this.poolSize = poolSize
        this.initialized = true
    }

    fun preload(context: Context) {
        if (!initialized) return
        repeat(poolSize - (loadedViews.size + loadingCount)) {
            preloadNext(context)
        }
    }

    private fun preloadNext(context: Context) {
        if (!initialized || adUnitId == null) return
        if (loadedViews.size + loadingCount >= poolSize) return

        loadingCount++
        val adWidthDp = getScreenWidthDp(context)
        val maxHeightDp = 330
        val adSize = BannerAdSize.inlineSize(context, adWidthDp, maxHeightDp)

        val bannerAdView = BannerAdView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setAdUnitId(adUnitId!!)
            setAdSize(adSize)
        }

        bannerAdView.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                retryAttempt = 0
                loadingCount--
                loadedViews.add(bannerAdView)
                log { "Yandex Banner Ad предзагружен (${loadedViews.size})" }
                
                onPreloadCallback?.invoke(
                    if (loadedViews.size >= poolSize) PreloadType.SUCCESS
                    else PreloadType.LOADING
                )
                preload(context)
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                loadingCount--
                retryAttempt++
                val delayMs = 2.0.pow(min(retryAttempt, 5)).toLong() * 1000
                log { "Ошибка предзагрузки Yandex Banner: ${error.description}. Повтор через $delayMs ms" }

                scope.launch {
                    delay(delayMs)
                    preloadNext(context)
                }
            }

            override fun onAdClicked() {}
            override fun onLeftApplication() {}
            override fun onReturnedToApplication() {}
            override fun onImpression(impressionData: ImpressionData?) {}
        })

        bannerAdView.loadAd(AdRequest.Builder().build())
    }

    fun popAd(context: Context): BannerAdView? {
        val adView = loadedViews.removeFirstOrNull()
        if (adView == null) {
            preload(context)
            return null
        }
        log { "Выдан предзагруженный Yandex Banner (осталось ${loadedViews.size})" }
        preload(context)
        return adView
    }

    private fun getScreenWidthDp(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return (metrics.widthPixels / metrics.density).roundToInt()
    }

    fun destroy() {
        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        loadedViews.forEach { it.destroy() }
        loadedViews.clear()
        initialized = false
        loadingCount = 0
    }

    private fun log(message: () -> String) = Log.d("YandexBannerPreloader", message())
}
