package ru.topbun.android.ads.open

import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import kotlinx.coroutines.*
import kotlin.math.min
import kotlin.math.pow

object ApplovinOpenAdManager : MaxAdListener {

    private var appOpenAd: MaxAppOpenAd? = null

    private var initialized = false
    private var paused = false

    private var isLoading = false
    private var isShowing = false

    private var retryAttempt = 0

    private var retryJob: Job? = null
    private var reloadJob: Job? = null
    private var loadTimeoutJob: Job? = null

    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var delaySeconds = 90
    private var lastShowTime = 0L

    private const val LOAD_TIMEOUT_MS = 15000L

    fun init(adId: String, delay: Int) {
        log { "init delay=$delay" }

        if (initialized) {
            log { "already initialized" }
            return
        }

        delaySeconds = delay
        initialized = true

        appOpenAd = MaxAppOpenAd(adId).apply {
            setListener(this@ApplovinOpenAdManager)
        }

        load()
    }

    fun showIfReady() {
        log { "showIfReady called" }

        if (!initialized) {
            log { "not initialized" }
            return
        }

        if (paused) {
            log { "paused" }
            return
        }

        if (!canShow()) {
            log { "cooldown active" }
            return
        }

        val ad = appOpenAd ?: run {
            log { "ad null" }
            return
        }

        if (ad.isReady && !isShowing) {
            log { "showing ad" }
            isShowing = true
            ad.showAd()
        } else {
            log { "not ready or already showing (ready=${ad.isReady})" }
        }
    }

    private fun canShow(): Boolean {
        val now = System.currentTimeMillis()
        val result = now - lastShowTime >= delaySeconds * 1000L
        log { "cooldown check = $result" }
        return result
    }

    private fun load() {

        if (!initialized || paused) {
            log { "load skipped initialized=$initialized paused=$paused" }
            return
        }

        if (isLoading) {
            log { "load skipped already loading" }
            return
        }

        val ad = appOpenAd ?: run {
            log { "ad null cannot load" }
            return
        }

        log { "start loading AppOpen" }

        isLoading = true

        startLoadTimeout()

        ad.loadAd()
    }

    private fun startLoadTimeout() {
        loadTimeoutJob?.cancel()

        loadTimeoutJob = scope.launch {
            delay(LOAD_TIMEOUT_MS)

            if (isLoading) {
                log { "load timeout -> restart load" }

                isLoading = false
                load()
            }
        }
    }

    override fun onAdLoaded(ad: MaxAd) {
        log { "onAdLoaded" }

        loadTimeoutJob?.cancel()

        isLoading = false
        retryAttempt = 0
        retryJob?.cancel()
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        log { "onAdLoadFailed ${error.message}" }

        loadTimeoutJob?.cancel()

        isLoading = false
        scheduleRetry()
    }

    override fun onAdDisplayed(ad: MaxAd) {
        log { "onAdDisplayed" }
    }

    override fun onAdHidden(ad: MaxAd) {
        log { "onAdHidden" }

        isShowing = false
        lastShowTime = System.currentTimeMillis()

        scheduleNextLoad()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        log { "onAdDisplayFailed ${error.message}" }

        isShowing = false
        scheduleRetry()
    }

    override fun onAdClicked(ad: MaxAd) {
        log { "onAdClicked" }
    }

    private fun scheduleNextLoad() {

        reloadJob?.cancel()

        val reloadDelay = if (delaySeconds > 10) 10L else delaySeconds.toLong()

        log { "scheduleNextLoad in $reloadDelay sec" }

        reloadJob = scope.launch {

            delay(reloadDelay * 1000)

            if (!paused && initialized) {
                load()
            }
        }
    }

    private fun scheduleRetry() {

        retryAttempt++

        val delayMs = 2.0.pow(min(retryAttempt, 5)).toLong() * 1000

        log { "retry in $delayMs ms attempt=$retryAttempt" }

        retryJob?.cancel()

        retryJob = scope.launch {

            delay(delayMs)

            if (!paused && initialized) {
                load()
            }
        }
    }

    fun pause() {
        log { "pause manager" }
        paused = true
    }

    fun resume() {
        log { "resume manager" }

        paused = false

        if (isLoading) {
            log { "loading stuck -> reset" }
            isLoading = false
        }

        if (appOpenAd?.isReady != true) {
            load()
        }
    }

    fun destroy() {

        log { "destroy manager" }

        retryJob?.cancel()
        reloadJob?.cancel()
        loadTimeoutJob?.cancel()

        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        appOpenAd?.setListener(null)
        appOpenAd?.destroy()
        appOpenAd = null

        retryAttempt = 0
        isLoading = false
        isShowing = false
        paused = false
        initialized = false
    }

    private fun log(message: () -> String) {
        Log.d("APPLOVIN_OPEN", message())
    }
}