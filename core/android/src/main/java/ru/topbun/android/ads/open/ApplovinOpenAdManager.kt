package ru.topbun.android.ads.open

import android.app.Activity
import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow

object ApplovinOpenAdManager : MaxAdListener {

    private var appOpenAd: MaxAppOpenAd? = null

    private var initialized = false
    private var isLoading = false
    private var isShowing = false
    private var paused = false

    private var retryAttempt = 0
    private var retryJob: Job? = null
    private var reloadJob: Job? = null

    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var delaySeconds = 90
    private var lastShowTime = 0L

    fun init(adId: String, delay: Int) {
        log { "Инициализация AppOpen, delay=$delay" }

        if (initialized) {
            log { "Уже инициализировано" }
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
        log { "Попытка показа AppOpen" }

        if (!initialized) {
            log { "Менеджер не инициализирован" }
            return
        }

        if (paused) {
            log { "Показ невозможен — пауза" }
            return
        }

        if (!canShow()) {
            log { "Не прошёл cooldown" }
            return
        }

        val ad = appOpenAd ?: run {
            log { "Реклама отсутствует" }
            return
        }

        if (ad.isReady && !isShowing) {
            log { "Реклама готова — показываем" }
            isShowing = true
            ad.showAd()
        } else {
            log { "Реклама не готова или уже показывается" }
        }
    }

    private fun canShow(): Boolean {
        val now = System.currentTimeMillis()
        val result = now - lastShowTime >= delaySeconds * 1000L
        log { "Проверка cooldown: $result" }
        return result
    }

    private fun load() {
        if (!initialized || isLoading || paused) {
            log { "Загрузка пропущена (initialized=$initialized isLoading=$isLoading paused=$paused)" }
            return
        }

        log { "Начинаем загрузку AppOpen" }
        isLoading = true
        appOpenAd?.loadAd()
    }

    override fun onAdLoaded(ad: MaxAd) {
        log { "AppOpen успешно загружена" }
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
        isShowing = false
        lastShowTime = System.currentTimeMillis()
        scheduleNextLoadAfterClose()
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        log { "Ошибка показа: ${error.message}" }
        isShowing = false
        scheduleRetry()
    }

    override fun onAdClicked(ad: MaxAd) {
        log { "Пользователь кликнул по рекламе" }
    }

    private fun scheduleNextLoadAfterClose() {
        reloadJob?.cancel()

        val reloadDelaySeconds =
            if (delaySeconds > 10) 10L else delaySeconds.toLong()

        log { "Следующая загрузка через $reloadDelaySeconds секунд" }

        reloadJob = scope.launch {
            delay(reloadDelaySeconds * 1000L)
            if (!paused && initialized) {
                log { "Запускаем отложенную загрузку" }
                load()
            }
        }
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
                log { "Запускаем повторную загрузку" }
                load()
            }
        }
    }

    fun pause() {
        log { "Менеджер переведён в паузу" }
        paused = true
    }

    fun resume() {
        log { "Менеджер возобновлён" }
        paused = false
        if (appOpenAd?.isReady != true) {
            load()
        }
    }

    fun destroy() {
        log { "Очистка AppOpen менеджера" }

        retryJob?.cancel()
        reloadJob?.cancel()
        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        appOpenAd?.destroy()
        appOpenAd?.setListener(null)
        appOpenAd = null

        retryAttempt = 0
        isLoading = false
        isShowing = false
        initialized = false
        paused = false
    }

    private fun log(message: () -> String) {
        Log.d("APPLOVIN_OPEN", message())
    }
}

