package ru.topbun.android.ads.inter

import android.app.Activity
import android.content.Context
import android.util.Log
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import kotlinx.coroutines.*
import kotlin.math.min
import kotlin.math.pow


object YandexInterAdManager : InterstitialAdLoadListener, InterstitialAdEventListener {

    private lateinit var loader: InterstitialAdLoader
    private var interAd: InterstitialAd? = null
    private var adUnitId: String = ""

    private var initialized = false

    @Volatile private var isLoading = false
    @Volatile private var paused = false

    private var retryAttempt = 0

    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var retryJob: Job? = null

    private var onAdReady: (() -> Unit)? = null

    fun setOnAdReadyCallback(callback: () -> Unit) {
        onAdReady = callback
    }

    fun clearCallback() {
        onAdReady = null
    }

    fun init(context: Context, adId: String) {
        log { "Инициализация Yandex Interstitial с adId=$adId" }

        if (initialized) {
            log { "Уже инициализировано — пропуск" }
            return
        }
        initialized = true

        adUnitId = adId
        loader = InterstitialAdLoader(context)
        loader.setAdLoadListener(this)

        load()
    }

    private fun load() {
        if (!initialized) {
            return
        }

        if (paused) {
            log { "Загрузка отменена — менеджер в паузе" }
            return
        }

        if (isLoading) {
            log { "Загрузка уже идёт — новая отменена" }
            return
        }

        if (interAd != null) {
            log { "Реклама уже загружена — повторная загрузка не требуется" }
            return
        }

        log { "Началась загрузка Yandex Interstitial Ad" }
        isLoading = true

        retryJob?.cancel()

        val request = AdRequestConfiguration.Builder(adUnitId).build()
        loader.loadAd(request)
    }

    fun showInterstitial(activity: Activity) {
        log { "Попытка показать Yandex Interstitial" }

        val ad = interAd
        if (ad != null) {
            log { "Реклама готова — показываем" }
            ad.setAdEventListener(this)
            ad.show(activity)
        } else {
            log { "Реклама не готова — запускаем загрузку" }
            load()
        }
    }

    override fun onAdLoaded(ad: InterstitialAd) {
        log { "Yandex Interstitial успешно загружена" }
        interAd = ad
        isLoading = false
        retryAttempt = 0
        onAdReady?.invoke()
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        log { "Ошибка загрузки: ${error.description}" }
        interAd = null
        isLoading = false

        retryAttempt++

        val delayMs = (2.0.pow(min(6, retryAttempt))).toLong() * 1000
        log { "Следующая попытка загрузки через $delayMs ms" }

        retryJob?.cancel()
        retryJob = scope.launch {
            delay(delayMs)
            load()
        }
    }

    override fun onAdShown() {
        log { "Yandex Interstitial показана" }
    }

    override fun onAdDismissed() {
        log { "Yandex Interstitial закрыта" }
        interAd = null

        if (!paused) {
            load()
        } else {
            log { "Не загружаем — менеджер в паузе" }
        }
    }

    override fun onAdFailedToShow(error: AdError) {
        log { "Ошибка показа: ${error.description}" }
        interAd = null
        isLoading = false
        load()
    }

    override fun onAdClicked() {
        log { "Yandex Interstitial кликнута" }
    }

    override fun onAdImpression(data: ImpressionData?) {
        log { "Yandex Interstitial Impression" }
    }

    fun onStop() {
        log { "PAUSE — отменяем загрузки, ставим paused=true" }
        paused = true
        retryJob?.cancel()
        isLoading = false
    }

    fun resume() {
        log { "RESUME — проверяем готовность и догружаем при необходимости" }

        paused = false

        if (interAd == null) {
            load()
        }
    }

    fun destroy() {
        log { "Destroy — чистим слушатели и корутины" }
        paused = true
        retryJob?.cancel()
        isLoading = false

        interAd?.setAdEventListener(null)
        interAd = null

        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        initialized = false
    }

    private fun log(msg: () -> String) {
        Log.d("YANDEX_INTER_AD", msg())
    }
}

