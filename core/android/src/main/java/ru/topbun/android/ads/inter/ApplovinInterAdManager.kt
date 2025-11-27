package ru.topbun.android.ads.inter

import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow

object ApplovinInterAdManager : MaxAdListener {

    private lateinit var interAd: MaxInterstitialAd
    private var initialized = false

    @Volatile private var isLoading = false
    @Volatile private var paused = false

    private var retryAttempt = 0

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var retryJob: Job? = null

    private var onAdReady: (() -> Unit)? = null

    fun setOnAdReadyCallback(callback: () -> Unit) {
        onAdReady = callback
    }

    fun clearCallback() {
        onAdReady = null
    }

    fun init(context: Context, adId: String) {
        log { "Инициализация Interstitial Ad с adId=$adId" }
        if (initialized) {
            log { "Уже инициализировано — пропуск" }
            return
        }
        initialized = true

        interAd = MaxInterstitialAd(adId, context)
        interAd.setListener(this)

        loadInterstitial()
    }

    private fun loadInterstitial() {

        if (!::interAd.isInitialized){
            log { "Реклама еще не инициализирована" }
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

        if (::interAd.isInitialized && interAd.isReady) {
            log { "Реклама уже загружена — повторная загрузка не требуется" }
            return
        }

        log { "Началась загрузка Interstitial Ad" }
        isLoading = true

        retryJob?.cancel()
        interAd.loadAd()
    }

    fun showInterstitial() {
        log { "Попытка показать Interstitial Ad" }
        if (::interAd.isInitialized && interAd.isReady) {
            log { "Interstitial Ad готова — показываем" }
            interAd.showAd()
        } else {
            log { "Не готова — запускаем загрузку" }
            loadInterstitial()
        }
    }

    override fun onAdLoaded(ad: MaxAd) {
        log { "Interstitial Ad успешно загружена" }
        isLoading = false
        retryAttempt = 0
        onAdReady?.invoke()
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        log { "Ошибка загрузки: ${error.message}" }
        isLoading = false
        retryAttempt++

        val delayMs = (2.0.pow(min(6, retryAttempt))).toLong() * 1000
        log { "Следующая попытка через $delayMs ms" }

        retryJob?.cancel()
        retryJob = scope.launch {
            delay(delayMs)
            loadInterstitial()
        }
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        log { "Ошибка показа: ${error.message}" }
        isLoading = false
        loadInterstitial()
    }

    override fun onAdDisplayed(ad: MaxAd) {
        log { "Interstitial Ad отображена" }
    }

    override fun onAdHidden(ad: MaxAd) {
        log { "Interstitial Ad скрыта" }
        isLoading = false

        if (!paused) {
            loadInterstitial()
        } else {
            log { "Не загружаем — менеджер в паузе" }
        }
    }

    override fun onAdClicked(ad: MaxAd) {
        log { "Interstitial Ad кликнута" }
    }

    fun onStop() {
        log { "PAUSE — отменяем загрузки и ставим флаг paused=true" }
        paused = true
        retryJob?.cancel()

        isLoading = false
    }

    fun resume() {
        log { "RESUME — проверяем готовность и загружаем при необходимости" }

        paused = false

        if (::interAd.isInitialized && !interAd.isReady) {
            loadInterstitial()
        }
    }

    fun destroy() {
        log { "Destroy — очищаем listener и корутины" }
        paused = true
        retryJob?.cancel()
        isLoading = false

        if (::interAd.isInitialized) {
            interAd.setListener(null)
        }

        scope.coroutineContext.cancelChildren()
    }

    private fun log(msg: () -> String) {
        Log.d("APPLOVIN_INTER_AD", msg())
    }
}