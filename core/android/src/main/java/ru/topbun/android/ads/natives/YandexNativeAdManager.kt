package ru.topbun.android.ads.natives

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdEventListener
import com.yandex.mobile.ads.nativeads.NativeAdException
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration
import com.yandex.mobile.ads.nativeads.NativeAdView
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.topbun.android.R
import kotlin.math.min
import kotlin.math.pow

object YandexNativeAdManager {

    private const val POOL_SIZE = 5

    private var adLoader: NativeAdLoader? = null
    private val loadedAdViews = ArrayDeque<NativeAdView>(POOL_SIZE)
    private var adUnitId: String? = null

    private var initialized = false
    private var retryAttempt = 0
    private var loadingCount = 0

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun init(context: Context, adUnitId: String) {
        log { "Инициализация Yandex Native Ad с adUnitId=$adUnitId" }
        if (initialized) return
        YandexNativeAdManager.adUnitId = adUnitId
        initialized = true

        adLoader = NativeAdLoader(context).apply {
            setNativeAdLoadListener(object : NativeAdLoadListener {
                override fun onAdLoaded(ad: NativeAd) {
                    retryAttempt = 0
                    loadingCount--

                    val adView = createAdView(context)
                    bindAdToView(ad, adView)

                    if (loadedAdViews.size < POOL_SIZE) {
                        loadedAdViews.add(adView)
                        log { "Native AdView добавлен в пул (текущий размер: ${loadedAdViews.size})" }
                    } else {
                        log { "Пул переполнен, лишняя Native Ad пропущена" }
                    }

                    preloadNext()
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    retryAttempt++
                    loadingCount--
                    val delayMs = 2.0.pow(min(retryAttempt, 6)).toLong() * 1000
                    log { "Ошибка загрузки Yandex Native Ad: ${error.description}. Повтор через $delayMs ms" }

                    scope.launch {
                        delay(delayMs)
                        preloadNext()
                    }
                }
            })
        }
    }

    private fun createAdView(context: Context): NativeAdView {
        return LayoutInflater.from(context).inflate(R.layout.yandex_native_ad_container, null) as NativeAdView
    }

    private fun bindAdToView(ad: NativeAd, adView: NativeAdView) {
        val binder = NativeAdViewBinder.Builder(adView)
            .setTitleView(adView.findViewById(R.id.title))
            .setDomainView(adView.findViewById(R.id.domain))
            .setWarningView(adView.findViewById(R.id.warning))
            .setSponsoredView(adView.findViewById(R.id.sponsored))
            .setFeedbackView(adView.findViewById(R.id.feedback))
            .setCallToActionView(adView.findViewById(R.id.call_to_action))
            .setMediaView(adView.findViewById(R.id.media))
            .setIconView(adView.findViewById(R.id.icon))
            .setPriceView(adView.findViewById(R.id.price))
            .setBodyView(adView.findViewById(R.id.body))
            .build()

        try {
            ad.bindNativeAd(binder)
            ad.setNativeAdEventListener(object : NativeAdEventListener {
                override fun onAdClicked() {
                    log { "Yandex Native Ad clicked" }
                }

                override fun onLeftApplication() {}
                override fun onReturnedToApplication() {}
                override fun onImpression(impressionData: ImpressionData?) {
                    log { "Yandex Native Ad impression" }
                }
            })
        } catch (_: NativeAdException) {}
    }

    private fun preloadNext() {
        if (!initialized) return
        if (loadedAdViews.size + loadingCount >= POOL_SIZE) return

        loadingCount++
        log { "Запуск загрузки следующей Yandex Native Ad (текущий пул: ${loadedAdViews.size}, в процессе: $loadingCount)" }

        adUnitId?.let {
            val request = NativeAdRequestConfiguration.Builder(it)
                .build()

            adLoader?.loadAd(request)
        } ?: run {
            log{ "adUnitId == null" }
            return
        }

    }

    fun preload() {
        if (!initialized) {
            log { "Реклама еще не инициализирована" }
            return
        }

        log { "Старт предзагрузки Yandex Native Ads" }
        repeat(POOL_SIZE) { preloadNext() }
    }

    fun popAd(): NativeAdView? {
        if (!initialized) return null

        val adView = loadedAdViews.removeFirstOrNull()
        if (adView != null) {
            log { "Yandex Native AdView выдан из пула (осталось: ${loadedAdViews.size})" }
            preloadNext()
        } else {
            log { "Нет готовых Yandex Native Ads в пуле, вернется null" }
        }
        return adView
    }

    fun destroy() {
        log { "Destroy Yandex Native Ad Manager" }
        scope.coroutineContext.cancel()
        loadedAdViews.clear()
        adLoader = null
        initialized = false
        retryAttempt = 0
        loadingCount = 0
    }

    private fun log(message: () -> String) = Log.d("YANDEX_NATIVE_AD", message())
}
