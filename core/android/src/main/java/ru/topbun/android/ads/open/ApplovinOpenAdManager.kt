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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow

object ApplovinOpenAdManager : MaxAdListener {

    private lateinit var appOpenAd: MaxAppOpenAd
    private var retryAttempt = 0
    private var isShowing = false
    private var initialized = false

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)


    fun init(activity: Activity, adId: String) {
        log { "Инициализация AppOpenAd с adId=$adId" }

        if (initialized) {
            log { "Уже инициализирован — пропуск" }
            return
        }
        initialized = true

        appOpenAd = MaxAppOpenAd(adId, activity)
        appOpenAd.setListener(this)

        loadAd()
    }

    private fun loadAd() {
        log { "Начинаем загрузку AppOpenAd…" }
        appOpenAd.loadAd()
    }

    fun showIfReady() {
        log { "Проверяем готовность AppOpenAd…" }

        if (::appOpenAd.isInitialized && appOpenAd.isReady && !isShowing) {
            isShowing = true
            log { "Готово! Показываем AppOpenAd…" }
            appOpenAd.showAd()
        } else {
            log { "Реклама не готова или уже показывается" }
        }
    }

    override fun onAdLoaded(ad: MaxAd) {
        log { "AppOpenAd: успешно загружена" }
        retryAttempt = 0
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        retryAttempt++

        val delay = 2.0.pow(min(6, retryAttempt)).toLong() * 1000
        log { "Ошибка загрузки: ${error.message}. Повтор через $delay ms" }

        scope.launch {
            delay(delay)
            loadAd()
        }
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        log { "Ошибка показа: ${error.message}" }
        isShowing = false
        loadAd()
    }

    override fun onAdDisplayed(ad: MaxAd) {
        log { "AppOpenAd: отображена" }
    }

    override fun onAdHidden(ad: MaxAd) {
        log { "AppOpenAd: скрыта" }
        isShowing = false
        loadAd()
    }

    override fun onAdClicked(ad: MaxAd) {
        log { "AppOpenAd: кликнута" }
    }

    fun pause() {
        log { "Pause: отменяем корутины" }
        job.cancelChildren()
    }

    fun resume() {
        log { "Resume: если не готова — загружаем" }
        if (::appOpenAd.isInitialized && !appOpenAd.isReady) {
            loadAd()
        }
    }

    fun destroy() {
        log { "Destroy: очищаем listener и корутины" }
        job.cancel()

        if (::appOpenAd.isInitialized) {
            appOpenAd.setListener(null)
        }
        initialized = false
    }

    private fun log(message: () -> String) {
        Log.d("APPLOVIN_OPEN_AD", message())
    }
}
