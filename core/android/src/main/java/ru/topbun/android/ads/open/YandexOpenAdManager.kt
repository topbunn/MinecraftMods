package ru.topbun.android.ads.open

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlin.math.min

object YandexOpenAdManager : AppOpenAdLoadListener {

    private var loader: AppOpenAdLoader? = null
    private var ad: AppOpenAd? = null
    private var openAdId: String? = null

    private var initialized = false
    private var paused = false

    private var delaySeconds = 90
    private var lastShowTime = 0L

    private var retryAttempt = 0
    private val handler = Handler(Looper.getMainLooper())

    private const val MAX_BACKOFF_EXP = 5

    fun init(application: Application, adId: String, delay: Int) {
        log { "Инициализация Yandex AppOpen, delay=$delay" }

        if (initialized) {
            log { "Уже инициализировано" }
            return
        }

        delaySeconds = delay
        openAdId = adId
        initialized = true

        loader = AppOpenAdLoader(application)
        loader?.setAdLoadListener(this)

        load()
    }

    fun show(activity: Activity) {
        log { "Попытка показа Yandex AppOpen" }

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

        ad?.let {
            log { "Реклама готова — показываем" }
            lastShowTime = System.currentTimeMillis()
            it.setAdEventListener(eventListener)
            it.show(activity)
        } ?: log { "Реклама не готова" }
    }

    private fun canShow(): Boolean {
        val now = System.currentTimeMillis()
        val result = now - lastShowTime >= delaySeconds * 1000L
        log { "Проверка cooldown: $result" }
        return result
    }

    private fun load() {
        if (!initialized || paused || openAdId == null) {
            log { "Загрузка пропущена (initialized=$initialized paused=$paused)" }
            return
        }

        log { "Начинаем загрузку Yandex AppOpen" }

        openAdId?.let {
            val config = AdRequestConfiguration.Builder(it).build()
            loader?.loadAd(config)
        }
    }

    override fun onAdLoaded(appOpenAd: AppOpenAd) {
        log { "Yandex AppOpen успешно загружена" }
        retryAttempt = 0
        ad = appOpenAd
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        log { "Ошибка загрузки: ${error.description}" }
        ad?.setAdEventListener(null)
        ad = null
        scheduleRetry()
    }

    private val eventListener = object : AppOpenAdEventListener {

        override fun onAdShown() {
            log { "Реклама показана" }
        }

        override fun onAdDismissed() {
            log { "Реклама закрыта пользователем" }

            ad?.setAdEventListener(null)
            ad = null
            scheduleNextLoad()
        }

        override fun onAdFailedToShow(adError: AdError) {
            log { "Ошибка показа: ${adError.description}" }

            ad?.setAdEventListener(null)
            ad = null
            scheduleRetry()
        }

        override fun onAdClicked() {
            log { "Пользователь кликнул по рекламе" }
        }

        override fun onAdImpression(impressionData: ImpressionData?) {
            log { "Засчитан показ рекламы" }
        }
    }

    private fun scheduleNextLoad() {
        val reloadDelaySeconds =
            if (delaySeconds > 10) 10L else delaySeconds.toLong()

        log { "Следующая загрузка через $reloadDelaySeconds секунд" }

        handler.postDelayed({
            if (!paused && initialized) {
                log { "Запускаем отложенную загрузку" }
                load()
            }
        }, reloadDelaySeconds * 1000L)
    }

    private fun scheduleRetry() {
        retryAttempt++

        val delay =
            (1L shl min(retryAttempt, MAX_BACKOFF_EXP)) * 1000L

        log { "Повторная попытка через $delay мс (попытка=$retryAttempt)" }

        handler.postDelayed({
            if (!paused && initialized) {
                log { "Запускаем повторную загрузку" }
                load()
            }
        }, delay)
    }

    fun pause() {
        log { "Менеджер переведён в паузу" }
        paused = true
    }

    fun resume() {
        log { "Менеджер возобновлён" }
        paused = false
    }

    fun destroy() {
        log { "Очистка Yandex AppOpen менеджера" }

        handler.removeCallbacksAndMessages(null)
        ad?.setAdEventListener(null)
        ad = null
        initialized = false
        paused = false
        retryAttempt = 0
    }

    private fun log(message: () -> String) {
        Log.d("YANDEX_OPEN", message())
    }
}


