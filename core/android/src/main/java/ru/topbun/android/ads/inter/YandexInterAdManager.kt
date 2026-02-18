package ru.topbun.android.ads.inter

import android.app.Activity
import android.app.Application
import android.util.Log
import com.yandex.mobile.ads.common.*
import com.yandex.mobile.ads.interstitial.*
import kotlinx.coroutines.*
import kotlin.math.min
import kotlin.math.pow

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

    private var retryAttempt = 0
    private var retryJob: Job? = null
    private var reloadJob: Job? = null

    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var delaySeconds = 90

    fun init(application: Application, adId: String, delay: Int) {
        if (initialized) return

        log { "Инициализация Yandex Inter, задержка=$delay сек" }

        this.app = application
        this.adId = adId
        this.delaySeconds = delay

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

        log { "Начинаем загрузку Yandex Inter" }
        isLoading = true

        val request = AdRequestConfiguration.Builder(adId).build()
        loader?.loadAd(request)
    }

    fun show(activity: Activity) {
        val ad = interAd
        if (ad != null && !paused) {
            log { "Показываем Yandex Inter" }
            ad.setAdEventListener(this)
            ad.show(activity)
            interAd = null
        } else {
            log { "Реклама не готова" }
        }
    }

    override fun onAdLoaded(ad: InterstitialAd) {
        log { "Yandex Inter загружен" }
        retryAttempt = 0
        retryJob?.cancel()
        interAd = ad
        isLoading = false
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        log { "Ошибка загрузки: ${error.description}" }
        isLoading = false
        scheduleRetry()
    }

    override fun onAdShown() {
        log { "Yandex Inter показан" }
    }

    override fun onAdDismissed() {
        log { "Yandex Inter закрыт пользователем" }
        destroyAd()
        scheduleLoadAfterClose()
    }

    override fun onAdFailedToShow(error: AdError) {
        log { "Ошибка показа: ${error.description}" }
        destroyAd()
        scheduleRetry()
    }

    override fun onAdClicked() {
        log { "Пользователь кликнул по рекламе" }
    }

    override fun onAdImpression(data: ImpressionData?) {
        log { "Засчитан показ рекламы" }
    }

    private fun scheduleLoadAfterClose() {
        reloadJob?.cancel()

        val reloadDelay =
            if (delaySeconds > 10) 10L else delaySeconds.toLong()

        log { "Загрузка после закрытия через $reloadDelay сек" }

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
        log { "Повтор через $delayMs мс (попытка=$retryAttempt)" }

        retryJob?.cancel()
        retryJob = scope.launch {
            delay(delayMs)
            if (!paused && initialized) {
                load()
            }
        }
    }

    fun pause() { paused = true }
    fun resume() { paused = false }

    fun destroy() {
        retryJob?.cancel()
        reloadJob?.cancel()
        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        destroyAd()
        loader?.setAdLoadListener(null)
        loader = null

        retryAttempt = 0
        isLoading = false
        initialized = false
    }

    private fun destroyAd() {
        interAd?.setAdEventListener(null)
        interAd = null
    }

    private fun log(msg: () -> String) {
        Log.d("YANDEX_INTER", msg())
    }
}

