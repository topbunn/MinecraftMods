package ru.topbun.android.ads.inter

import android.app.Activity
import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.common.*
import com.yandex.mobile.ads.interstitial.*

object YandexInterAdManager :
    InterstitialAdLoadListener,
    InterstitialAdEventListener {

    private var loader: InterstitialAdLoader? = null
    private var interAd: InterstitialAd? = null

    private var initialized = false
    private var isLoading = false
    private var paused = false

    private lateinit var adUnitId: String

    private var onAdShownAction: (() -> Unit)? = null
    private var onAdClosedAction: (() -> Unit)? = null

    fun init(context: Context, adId: String) {
        if (initialized) return

        initialized = true
        adUnitId = adId

        loader = InterstitialAdLoader(context.applicationContext).apply {
            setAdLoadListener(this@YandexInterAdManager)
        }
    }

    fun isAdReady(): Boolean {
        return interAd != null && !paused
    }

    fun load() {
        if (!initialized || paused || isLoading || interAd != null) return

        log { "Load Yandex Interstitial" }
        isLoading = true

        val request = AdRequestConfiguration.Builder(adUnitId).build()
        loader?.loadAd(request)
    }

    fun show(activity: Activity) {
        val ad = interAd
        if (ad == null) {
            log { "Interstitial not ready" }
            return
        }

        log { "Show Yandex Interstitial" }
        interAd = null

        ad.setAdEventListener(this)
        ad.show(activity)
    }

    override fun onAdLoaded(ad: InterstitialAd) {
        log { "Yandex Interstitial loaded" }
        interAd = ad
        isLoading = false
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        log { "Load failed: ${error.description}" }
        interAd = null
        isLoading = false
        onAdClosedAction?.invoke()
    }

    override fun onAdShown() {
        log { "Yandex Interstitial displayed" }
        onAdShownAction?.invoke()
    }

    override fun onAdDismissed() {
        log { "Yandex Interstitial closed" }
        destroyAd()
        onAdClosedAction?.invoke()
    }

    override fun onAdFailedToShow(error: AdError) {
        log { "Show failed: ${error.description}" }
        destroyAd()
        onAdClosedAction?.invoke()
    }

    override fun onAdClicked() {
        log { "Yandex Interstitial clicked" }
    }

    override fun onAdImpression(data: ImpressionData?) {
        log { "Yandex Interstitial impression" }
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    fun destroy() {
        destroyAd()

        loader?.setAdLoadListener(null)
        loader = null

        initialized = false
        isLoading = false
        paused = false

        onAdShownAction = null
        onAdClosedAction = null
    }

    fun setOnAdShownAction(action: () -> Unit) {
        onAdShownAction = action
    }

    fun setOnAdClosedAction(action: () -> Unit) {
        onAdClosedAction = action
    }

    private fun destroyAd() {
        interAd?.setAdEventListener(null)
        interAd = null
    }

    private fun log(msg: () -> String) {
        Log.d("YANDEX_INTER", msg())
    }
}
