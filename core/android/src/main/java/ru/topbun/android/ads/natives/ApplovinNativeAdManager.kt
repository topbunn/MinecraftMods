package ru.topbun.android.ads.natives

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.widget.Button
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin
import ru.topbun.android.R
import ru.topbun.android.ads.natives.NativeAdInitializer.configProvider
import ru.topbun.domain.entity.modConfig.ModConfigProvider
import kotlin.math.min
import kotlin.math.pow

object ApplovinNativeAdManager {


    private const val POOL_SIZE = 5

    private lateinit var adLoader: MaxNativeAdLoader
    private val loadedAdViews = ArrayDeque<MaxNativeAdView>(POOL_SIZE)

    private var initialized = false
    private var retryAttempt = 0
    private var loadingCount = 0

    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun init(context: Context, adUnitId: String) {
        log { "Инициализация Native Ad с adUnitId=$adUnitId" }
        if (initialized) return
        initialized = true

        adLoader = MaxNativeAdLoader(adUnitId)
        adLoader.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd) {
                retryAttempt = 0
                loadingCount--

                if (nativeAdView != null) {
                    if (loadedAdViews.size < POOL_SIZE) {
                        loadedAdViews.add(nativeAdView)
                        log { "Native AdView добавлен в пул (текущий размер: ${loadedAdViews.size})" }
                    } else {
                        adLoader.destroy(nativeAd)
                        log { "Пул переполнен, лишняя Native Ad уничтожена" }
                    }
                }

                preloadNext(context.applicationContext)
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                retryAttempt++
                loadingCount--
                val delayMs = 2.0.pow(min(retryAttempt, 6)).toLong() * 1000
                log { "Ошибка загрузки Native Ad: ${error.message}. Повтор через $delayMs ms" }

                scope.launch {
                    delay(delayMs)
                    preloadNext(context.applicationContext)
                }
            }
        })
    }

    private fun preloadNext(context: Context) {
        scope.launch {
            if (loadedAdViews.size + loadingCount >= POOL_SIZE) {
                log { "Пул Native Ad полон, загрузка следующей отменена" }
                return@launch
            }

            loadingCount++
            log { "Запуск загрузки следующей Native Ad (текущий пул: ${loadedAdViews.size}, в процессе: $loadingCount)" }


            val binder = MaxNativeAdViewBinder.Builder(R.layout.applovin_native_ad_container)
                .setTitleTextViewId(R.id.applovin_title)
                .setBodyTextViewId(R.id.applovin_body)
                .setAdvertiserTextViewId(R.id.applovin_advertiser)
                .setIconImageViewId(R.id.applovin_icon)
                .setMediaContentViewGroupId(R.id.applovin_media)
                .setOptionsContentViewGroupId(R.id.applovin_options)
                .setCallToActionButtonId(R.id.applovin_call_to_action)
                .build()

            val adView = MaxNativeAdView(binder, context)

            adLoader.loadAd(adView)
        }
    }

    fun preload(context: Context) {
        if (!::adLoader.isInitialized) {
            log { "Реклама еще не инициализирована" }
            return
        }
        log { "Старт предзагрузки Native Ads" }
        repeat(POOL_SIZE) { preloadNext(context) }
    }

    fun popAd(context: Context): MaxNativeAdView? {
        val adView = loadedAdViews.removeFirstOrNull()
        if (adView != null) {
            log { "Native AdView выдан из пула (осталось: ${loadedAdViews.size})" }
            preloadNext(context.applicationContext)
        } else {
            log { "Нет готовых Native Ads в пуле, вернется null" }
        }
        return adView
    }

    fun destroy() {
        log { "Destroy Native Ad Manager" }

        scope.cancel()
        scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        loadedAdViews.clear()
        adLoader.setNativeAdListener(null)
        initialized = false
    }

    private fun log(message: () -> String) = Log.d("APPLOVIN_NATIVE_AD", message())
}

