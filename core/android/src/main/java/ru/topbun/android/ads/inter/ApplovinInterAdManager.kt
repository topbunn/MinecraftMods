package ru.topbun.android.ads.inter

import android.app.Activity
import android.app.Application
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.yandex.mobile.ads.common.*
import com.yandex.mobile.ads.interstitial.*
import kotlinx.coroutines.*
import kotlin.math.min
import kotlin.math.pow


object ApplovinInterAdManager : MaxAdListener {

    private var interAd: MaxInterstitialAd? = null

    private var initialized = false
    private var isLoading = false
    private var paused = false

    private var retryAttempt = 0
    private var retryJob: Job? = null
    private var reloadJob: Job? = null

    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var delaySeconds = 90
    private var lastShowTime = 0L

    fun init(adId: String, delay: Int) {
        log { "Инициализация Inter, задержка=$delay сек" }

        if (initialized) return

        delaySeconds = delay
        initialized = true

        interAd = MaxInterstitialAd(adId).apply {
            setListener(this@ApplovinInterAdManager)
        }
        load()
    }

    fun show(activity: Activity) {
        log { "Попытка показа Inter" }

        if (!initialized || paused) {
            log { "Показ невозможен (initialized=$initialized paused=$paused)" }
            return
        }

        if (!canShow()) {
            log { "Не прошёл интервал между показами" }
            return
        }

        val ad = interAd ?: run {
            log { "Реклама отсутствует" }
            return
        }

        if (ad.isReady) {
            log { "Реклама готова — показываем" }
            ad.showAd(activity)
        } else {
            log { "Реклама ещё не готова" }
        }
    }

    fun isAdReady(): Boolean {
        val ready = interAd?.isReady == true && !paused
        log { "Проверка готовности: $ready" }
        return ready
    }

    private fun canShow(): Boolean {
        val now = System.currentTimeMillis()
        val result = now - lastShowTime >= delaySeconds * 1000L
        log { "Проверка интервала: $result" }
        return result
    }

    private fun baseReloadDelaySeconds(): Long {
        return if (delaySeconds > 10) 10L else delaySeconds.toLong()
    }

    private fun scheduleLoadWithBaseDelay() {
        reloadJob?.cancel()

        val delaySec = baseReloadDelaySeconds()
        log { "Запланирована загрузка через $delaySec секунд" }

        reloadJob = scope.launch {
            delay(delaySec * 1000L)
            if (!paused && initialized) {
                load()
            }
        }
    }

    private fun load() {
        if (!initialized || paused || isLoading) {
            log { "Загрузка пропущена (initialized=$initialized paused=$paused isLoading=$isLoading)" }
            return
        }

        log { "Начинаем загрузку Inter" }
        isLoading = true
        interAd?.loadAd()
    }

    override fun onAdLoaded(ad: MaxAd) {
        log { "Inter успешно загружен" }
        retryAttempt = 0
        retryJob?.cancel()
        isLoading = false
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        log { "Ошибка загрузки: ${error.message}" }
        isLoading = false
        scheduleRetry()
    }

    override fun onAdDisplayed(ad: MaxAd) {
        log { "Реклама показана" }
    }

    override fun onAdHidden(ad: MaxAd) {
        log { "Реклама закрыта пользователем" }
        lastShowTime = System.currentTimeMillis()
        scheduleLoadWithBaseDelay()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        log { "Ошибка показа: ${error.message}" }
        scheduleRetry()
    }

    override fun onAdClicked(ad: MaxAd) {
        log { "Пользователь кликнул по рекламе" }
    }

    private fun scheduleRetry() {
        retryAttempt++

        val delayMs =
            2.0.pow(min(retryAttempt, 5)).toLong() * 1000

        log { "Повторная попытка через $delayMs мс (попытка=$retryAttempt)" }

        retryJob?.cancel()
        retryJob = scope.launch {
            delay(delayMs)
            if (!paused && initialized) {
                load()
            }
        }
    }

    fun pause() {
        log { "Менеджер Inter переведён в паузу" }
        paused = true
    }

    fun resume() {
        log { "Менеджер Inter возобновлён" }
        paused = false
    }

    fun destroy() {
        log { "Очистка Inter менеджера" }

        retryJob?.cancel()
        reloadJob?.cancel()
        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        interAd?.destroy()
        interAd?.setListener(null)
        interAd = null

        retryAttempt = 0
        isLoading = false
        initialized = false
        paused = false
    }

    private fun log(message: () -> String) {
        Log.d("APPLOVIN_INTER", message())
    }
}



