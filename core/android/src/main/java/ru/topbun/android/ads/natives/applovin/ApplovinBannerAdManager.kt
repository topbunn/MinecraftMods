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
import kotlin.math.min
import kotlin.math.pow

class ApplovinBannerAdManager(
    private val adUnitId: String,
    private val onLoadedStateChanged: (Boolean) -> Unit
) : MaxAdViewAdListener {

    private var adView: MaxAdView? = null
    private var retryAttempt = 0
    private var isLoading = false
    val maxHeightDp = 330

    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attach(context: Context, container: ViewGroup) {
        if (adView != null) return
        log { "Создание INLINE Adaptive MREC instance" }
        val appContext = context.applicationContext

        val screenWidthDp = context.resources.configuration.screenWidthDp


        val config = MaxAdViewConfiguration.builder()
            .setAdaptiveType(MaxAdViewConfiguration.AdaptiveType.INLINE)
            .setAdaptiveWidth(screenWidthDp)
            .setInlineMaximumHeight(maxHeightDp)
            .build()

        adView = MaxAdView(adUnitId, MaxAdFormat.MREC, config).apply {

            setListener(this@ApplovinBannerAdManager)

            val heightPx = AppLovinSdkUtils.dpToPx(appContext, adView?.height ?: maxHeightDp)

            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                heightPx
            )

            setBackgroundColor(Color.TRANSPARENT)

            setExtraParameter("allow_pause_auto_refresh_immediately", "true")
            stopAutoRefresh()

            container.addView(this)
        }

        load()
    }

    private fun load() {
        if (isLoading) return

        log { "Загрузка MREC..." }

        isLoading = true
        adView?.loadAd()
    }

    fun destroy() {
        log { "Destroy MREC instance" }

        scope.cancel()
        adView?.destroy()
        adView = null
    }

    override fun onAdLoaded(ad: MaxAd) {
        log { "MREC loaded" }
        retryAttempt = 0
        isLoading = false
        onLoadedStateChanged(true)
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        log { "Ошибка загрузки: ${error.message}" }

        isLoading = false
        onLoadedStateChanged(false)

        retryAttempt++

        val delayMs = 2.0.pow(min(retryAttempt, 5)).toLong() * 1000

        scope.launch {
            delay(delayMs)
            load()
        }
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        log { "Display failed: ${error.message}" }
    }

    override fun onAdClicked(ad: MaxAd) {
        log { "MREC clicked" }
    }

    override fun onAdExpanded(ad: MaxAd) {}
    override fun onAdCollapsed(ad: MaxAd) {}
    override fun onAdDisplayed(ad: MaxAd) {}
    override fun onAdHidden(ad: MaxAd) {}

    private fun log(message: () -> String) =
        Log.d("APPLOVIN_MREC", message())
}