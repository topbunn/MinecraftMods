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

    private lateinit var loader: AppOpenAdLoader
    private lateinit var adUnit: String

    private var ad: AppOpenAd? = null

    private var state = AdState.IDLE
    private var retryAttempt = 0
    private var lastLoadTime = 0L

    private val handler = Handler(Looper.getMainLooper())

    private const val MIN_LOAD_INTERVAL = 800L
    private const val BASE_BACKOFF = 1000L
    private const val MAX_BACKOFF_EXP = 6

    private enum class AdState {
        IDLE,
        LOADING,
        READY,
        SHOWING
    }

    fun init(application: Application, adId: String) {
        log{"Инициализация менеджера. AdUnit = $adId"}

        loader = AppOpenAdLoader(application)
        loader.setAdLoadListener(this)
        adUnit = adId

        load()
    }

    fun show(activity: Activity) {
        log{"Запрос на показ рекламы. state=$state, ad=${ad != null}"}

        if (state == AdState.READY && ad != null) {
            log{"Показываем рекламу"}
            state = AdState.SHOWING
            ad!!.setAdEventListener(eventListener)
            ad!!.show(activity)
        } else {
            log{"Реклама не готова, запускаем загрузку"}
            load()
        }
    }

    private fun load() {
        if (!::loader.isInitialized) {
            log { "Ошибка: loader не инициализирован" }
            return
        }

        if (state == AdState.LOADING) {
            log{"Загрузка уже идет — пропускаем"}
            return
        }

        val now = System.currentTimeMillis()

        if (now - lastLoadTime < MIN_LOAD_INTERVAL) {
            val wait = MIN_LOAD_INTERVAL - (now - lastLoadTime)
            log{"Слишком рано для повторной загрузки. Ждем $wait мс"}
            handler.postDelayed({ load() }, wait)
            return
        }

        log{"Начинаем загрузку рекламы"}
        state = AdState.LOADING

        val config = AdRequestConfiguration.Builder(adUnit).build()
        loader.loadAd(config)
    }

    override fun onAdLoaded(loadedAd: AppOpenAd) {
        log{"Реклама успешно загружена"}

        ad = loadedAd
        state = AdState.READY
        retryAttempt = 0
        lastLoadTime = System.currentTimeMillis()
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        log { "Ошибка загрузки рекламы: ${error.description}" }

        ad = null
        state = AdState.IDLE

        retryAttempt++
        val delay = calculateBackoffDelay(retryAttempt)

        log{"Повторная загрузка через $delay мс (попытка $retryAttempt)"}

        handler.postDelayed({ load() }, delay)
    }

    private fun calculateBackoffDelay(attempt: Int): Long {
        val exp = min(MAX_BACKOFF_EXP, attempt)
        val delay = BASE_BACKOFF * (1L shl exp)
        log{"Бэкофф задержка: $delay мс"}
        return delay
    }

    private val eventListener = object : AppOpenAdEventListener {

        override fun onAdShown() {
            log{"Реклама отображается"}
            state = AdState.SHOWING
        }

        override fun onAdDismissed() {
            log{"Реклама закрыта пользователем"}
            ad = null
            state = AdState.IDLE
            load()
        }

        override fun onAdFailedToShow(error: AdError) {
           log { "Ошибка показа рекламы: ${error.description}" }
            ad = null
            state = AdState.IDLE
            handler.postDelayed({ load() }, BASE_BACKOFF)
        }

        override fun onAdClicked() {
            log{"Реклама кликнута"}
        }

        override fun onAdImpression(data: ImpressionData?) {
            log{"Засчитан показ рекламы"}
        }
    }

    fun destroy() {
        log{"Очистка ресурсов менеджера"}

        ad?.setAdEventListener(null)
        ad = null
        state = AdState.IDLE
        retryAttempt = 0
        handler.removeCallbacksAndMessages(null)
    }

    private fun log(message: () -> String) {
        Log.d("YANDEX_OPEN_AD", message())
    }
}
