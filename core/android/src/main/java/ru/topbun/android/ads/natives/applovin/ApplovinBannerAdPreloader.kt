package ru.topbun.android.ads.natives.applovin

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxAdViewConfiguration
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.topbun.android.ads.natives.NativeAdInitializer.PreloadType
import kotlin.math.min
import kotlin.math.pow

object ApplovinBannerAdPreloader {

    private var poolSize = 1
    private val loadedViews = ArrayDeque<MaxAdView>(poolSize)
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
        
        val screenWidthDp = context.resources.configuration.screenWidthDp
        val maxHeightDp = 330

        val config = MaxAdViewConfiguration.builder()
            .setAdaptiveType(MaxAdViewConfiguration.AdaptiveType.INLINE)
            .setAdaptiveWidth(screenWidthDp)
            .setInlineMaximumHeight(maxHeightDp)
            .build()

        val adView = MaxAdView(adUnitId, MaxAdFormat.MREC, config)

        adView.setListener(object : MaxAdViewAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                retryAttempt = 0
                loadingCount--
                loadedViews.add(adView)
                log { "Applovin Banner Ad предзагружен (${loadedViews.size})" }
                
                onPreloadCallback?.invoke(
                    if (loadedViews.size >= poolSize) PreloadType.SUCCESS
                    else PreloadType.LOADING
                )
                preload(context)
            }

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                loadingCount--
                retryAttempt++
                val delayMs = 2.0.pow(min(retryAttempt, 5)).toLong() * 1000
                log { "Ошибка предзагрузки Applovin Banner: ${error.message}. Повтор через $delayMs ms" }

                scope.launch {
                    delay(delayMs)
                    preloadNext(context)
                }
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
            override fun onAdClicked(ad: MaxAd) {}
            override fun onAdExpanded(ad: MaxAd) {}
            override fun onAdCollapsed(ad: MaxAd) {}
            override fun onAdDisplayed(ad: MaxAd) {}
            override fun onAdHidden(ad: MaxAd) {}
        })

        val heightPx = AppLovinSdkUtils.dpToPx(context, maxHeightDp)
        adView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            heightPx
        )
        adView.setBackgroundColor(Color.TRANSPARENT)
        adView.setExtraParameter("allow_pause_auto_refresh_immediately", "true")
        adView.stopAutoRefresh()

        adView.loadAd()
    }

    fun popAd(context: Context): MaxAdView? {
        val adView = loadedViews.removeFirstOrNull()
        if (adView == null) {
            preload(context)
            return null
        }
        log { "Выдан предзагруженный Applovin Banner (осталось ${loadedViews.size})" }
        preload(context)
        return adView
    }

    fun destroy() {
        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        loadedViews.forEach { it.destroy() }
        log { "Destroy, очищено ${loadedViews.size} баннеров" }
        loadedViews.clear()
        initialized = false
        loadingCount = 0
    }

    private fun log(message: () -> String) = Log.d("ApplovinBannerPreloader", message())
}
