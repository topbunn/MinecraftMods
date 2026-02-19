package ru.topbun.android.ads.banner.applovin

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdViewAdListener
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

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attach(context: Context): MaxAdView {
        if (adView != null) return adView!!
        val appContext = context.applicationContext
        val adView = MaxAdView(adUnitId, MaxAdFormat.BANNER, context)
        adView.setListener(this)
        val widthPx = ViewGroup.LayoutParams.MATCH_PARENT
        val heightPx = AppLovinSdkUtils.dpToPx(appContext, 70)
        adView.layoutParams = FrameLayout.LayoutParams(widthPx, heightPx)
        adView.setBackgroundColor(Color.TRANSPARENT)
        this.adView = adView
        load()
        return adView
    }

    private fun load() {
        if (isLoading) return
        isLoading = true
        adView?.loadAd()
    }

    fun destroy() {
        scope.cancel()
        adView?.destroy()
        adView = null
    }

    override fun onAdLoaded(ad: MaxAd) {
        retryAttempt = 0
        isLoading = false
        onLoadedStateChanged(true)
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        isLoading = false
        onLoadedStateChanged(false)

        retryAttempt++
        val delayMs = 2.0.pow(min(retryAttempt, 5)).toLong() * 1000

        scope.launch {
            delay(delayMs)
            load()
        }
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
    override fun onAdClicked(ad: MaxAd) {}
    override fun onAdExpanded(ad: MaxAd) {}
    override fun onAdCollapsed(ad: MaxAd) {}
    override fun onAdDisplayed(ad: MaxAd) {}
    override fun onAdHidden(ad: MaxAd) {}
}