package ru.topbun.android.ads.banner.yandex

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class YandexBannerAdManager(
    private val adUnitId: String,
    private val onLoadedStateChanged: (Boolean) -> Unit
) : BannerAdEventListener {

    private var bannerAdView: BannerAdView? = null
    private var retryAttempt = 0
    private var isLoading = false

    private var scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun attach(context: Context, container: ViewGroup) {
        if (bannerAdView != null) return

        log { "Создание Yandex banner instance" }

        val adWidthDp = getScreenWidthDp(context)
        val adSize = BannerAdSize.stickySize(context, adWidthDp)

        bannerAdView = BannerAdView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setAdUnitId(adUnitId)
            setAdSize(adSize)
            setBannerAdEventListener(this@YandexBannerAdManager)

            container.addView(this)
        }

        load()
    }

    private fun load() {
        if (isLoading) return

        log { "Загрузка Yandex banner..." }

        isLoading = true

        bannerAdView?.loadAd(
            AdRequest.Builder().build()
        )
    }

    fun destroy() {
        log { "Destroy Yandex banner" }

        scope.cancel()
        bannerAdView?.destroy()
        bannerAdView = null
    }

    override fun onAdLoaded() {
        log { "Yandex banner loaded" }

        retryAttempt = 0
        isLoading = false
        onLoadedStateChanged(true)
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        log { "Ошибка загрузки: ${error.description}" }

        isLoading = false
        onLoadedStateChanged(false)

        retryAttempt++

        val delayMs = 2.0.pow(min(retryAttempt, 5)).toLong() * 1000

        scope.launch {
            delay(delayMs)
            load()
        }
    }

    override fun onAdClicked() {
        log { "Banner clicked" }
    }

    override fun onLeftApplication() {}
    override fun onReturnedToApplication() {}

    override fun onImpression(impressionData: ImpressionData?) {
        log { "Impression recorded" }
    }

    private fun getScreenWidthDp(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return (metrics.widthPixels / metrics.density).roundToInt()
    }

    private fun log(message: () -> String) =
        Log.d("YANDEX_BANNER", message())
}
