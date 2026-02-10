package ru.topbun.android.ads.inter

import android.app.Activity
import android.app.Application
import android.util.Log
import com.applovin.mediation.*
import com.applovin.mediation.ads.MaxInterstitialAd
import kotlinx.coroutines.*

object ApplovinInterAdManager : MaxAdListener {

    private var interAd: MaxInterstitialAd? = null
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
    }

    fun isAdReady(): Boolean {
        return interAd?.isReady == true && !paused
    }

    fun load() {
        if (paused || isLoading || interAd != null) return

        log { "Load Interstitial" }
        isLoading = true

        interAd = MaxInterstitialAd(adId, app).apply {
            setListener(this@ApplovinInterAdManager)
            loadAd()
        }
    }

    fun show(activity: Activity) {
        val ad = interAd
        if (ad?.isReady == true) {
            log { "Show Interstitial" }
            ad.showAd(activity)
        } else {
            log { "Interstitial not ready" }
        }
    }

    override fun onAdLoaded(ad: MaxAd) {
        log { "Interstitial loaded" }
        isLoading = false
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        log { "Load failed: ${error.message}" }
        destroyAd()
        isLoading = false
        onAdClosedAction?.invoke()
    }

    override fun onAdDisplayed(ad: MaxAd) {
        log { "Interstitial displayed" }
        onAdShownAction?.invoke()
    }

    override fun onAdHidden(ad: MaxAd) {
        log { "Interstitial closed" }
        destroyAd()
        onAdClosedAction?.invoke()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        log { "Display failed: ${error.message}" }
        destroyAd()
        onAdClosedAction?.invoke()
    }

    override fun onAdClicked(ad: MaxAd) {}

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    fun destroy() {
        destroyAd()
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
        interAd?.setListener(null)
        interAd = null
    }

    private fun log(msg: () -> String) {
        Log.d("APPLOVIN_INTER", msg())
    }
}
