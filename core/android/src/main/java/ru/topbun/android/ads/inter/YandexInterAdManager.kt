package ru.topbun.android.ads.inter

import android.app.Activity
import android.app.Application
import android.util.Log
import com.yandex.mobile.ads.common.*
import com.yandex.mobile.ads.interstitial.*
import kotlinx.coroutines.*

object YandexInterAdManager :
    InterstitialAdLoadListener,
    InterstitialAdEventListener {

    private var loader: InterstitialAdLoader? = null
    private var interAd: InterstitialAd? = null

    private var initialized = false
    private var isLoading = false
    private var paused = false

    private lateinit var app: Application
    private lateinit var adId: String

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var onAdShownAction: (() -> Unit)? = null
    private var onAdClosedAction: (() -> Unit)? = null

    fun init(application: Application, adId: String) {
        if (initialized) return

        this.app = application
        this.adId = adId
        initialized = true

        loader = InterstitialAdLoader(application).apply {
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

        val request = AdRequestConfiguration.Builder(adId).build()
        loader?.loadAd(request)
    }

    fun show(activity: Activity) {
        val ad = interAd
        if (ad != null) {
            log { "Show Yandex Interstitial" }
            ad.setAdEventListener(this)
            ad.show(activity)
            interAd = null
        } else {
            log { "Interstitial not ready" }
        }
    }

    override fun onAdLoaded(ad: InterstitialAd) {
        log { "Yandex Interstitial loaded" }
        interAd = ad
        isLoading = false
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        log { "Load failed: ${error.description}" }
        destroyAd()
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
        scope.cancel()
        initialized = false
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
