package ru.topbun.ui.components

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.sdk.AppLovinSdk
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import ru.topbun.android.DefaultProcessLifecycleObserver
import ru.topbun.android.utills.LocationAd
import ru.topbun.android.utills.getLocation
import ru.topbun.ui.BuildConfig
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun OpenAppAd(activity: Activity, isEnabledAd: Boolean, yandexId: String?, applovinId: String?) {
    if (isEnabledAd){
        val location = activity.applicationContext.getLocation()
        when(location){
            LocationAd.RU -> {
                yandexId?.let {
                    OpenAppAd.Yandex(activity, yandexId).apply { initialize() }
                }
            }
            LocationAd.OTHER -> {
                applovinId?.let {
                    OpenAppAd.Applovin(activity, applovinId).apply { initialize() }
                }
            }
        }
    }
}

sealed interface OpenAppAd{

    class Yandex(private val activity: Activity, adId: String) : AppOpenAdLoadListener, OpenAppAd {

        private val context: Context = activity.applicationContext

        private val processLifecycleObserver = DefaultProcessLifecycleObserver(
            onProcessCameForeground = { showAdIfAvailable(activity) }
        )

        private val appOpenAdLoader = AppOpenAdLoader(context)
        private val adRequestConfiguration = AdRequestConfiguration.Builder(adId).build()
        private var loadingInProgress = AtomicBoolean(false)
        private var appOpenAd: AppOpenAd? = null
        private var isFirstStartAd = true

        private val appOpenAdEventListener = AdEventListener()

        init {
            ProcessLifecycleOwner.get().lifecycle.addObserver(processLifecycleObserver)
        }

        fun initialize() {
            appOpenAdLoader.setAdLoadListener(this)
            loadAppOpenAd()
        }

        override fun onAdLoaded(appOpenAd: AppOpenAd) {
            this.appOpenAd = appOpenAd
            loadingInProgress.set(false)
            if (isFirstStartAd){
                showAdIfAvailable(activity)
                isFirstStartAd = false
            }
        }

        override fun onAdFailedToLoad(adRequestError: AdRequestError) {
            loadingInProgress.set(false)
        }

        private fun showAdIfAvailable(activity: Activity) {
            val appOpenAd = appOpenAd
            if (appOpenAd != null) {
                appOpenAd.setAdEventListener(appOpenAdEventListener)
                appOpenAd.show(activity)
            } else {
                loadAppOpenAd()
            }
        }

        private fun loadAppOpenAd() {
            if (loadingInProgress.compareAndSet(false, true)) {
                appOpenAdLoader.loadAd(adRequestConfiguration)
            }
        }

        private fun clearAppOpenAd() {
            appOpenAd?.setAdEventListener(null)
            appOpenAd = null
        }

        private inner class AdEventListener : AppOpenAdEventListener {
            override fun onAdShown() {
            }

            override fun onAdFailedToShow(adError: AdError) {
            }

            override fun onAdDismissed() {
                clearAppOpenAd()
                loadAppOpenAd()
            }

            override fun onAdClicked() {
            }

            override fun onAdImpression(impressionData: ImpressionData?) {
            }
        }

    }

    class Applovin(private val context: Context, private val adId: String): MaxAdListener, OpenAppAd {

        private val appOpenAd: MaxAppOpenAd by lazy { MaxAppOpenAd(adId) }
        private var isFirstStartAd = true

        private val processLifecycleObserver = DefaultProcessLifecycleObserver(
            onProcessCameForeground = { showAdIfReady() }
        )

        init
        {
            ProcessLifecycleOwner.get().lifecycle.addObserver(processLifecycleObserver)

            appOpenAd.setListener(this)
            appOpenAd.loadAd()
        }

        fun initialize(){
            if (isFirstStartAd){
                showAdIfReady()
                isFirstStartAd = false
            }
        }

        private fun showAdIfReady()
        {
            if (!AppLovinSdk.getInstance(context).isInitialized) return

            if (appOpenAd.isReady) {
                appOpenAd.showAd()
            } else {
                appOpenAd.loadAd()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart()
        {
            showAdIfReady()
        }

        override fun onAdLoaded(ad: MaxAd) {}
        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
        override fun onAdDisplayed(ad: MaxAd) {}
        override fun onAdClicked(ad: MaxAd) {}

        override fun onAdHidden(ad: MaxAd) {
            appOpenAd.loadAd()
        }

        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
            appOpenAd.loadAd()
        }
    }



}