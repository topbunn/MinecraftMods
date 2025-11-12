package ru.topbun.ui.components

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import kotlinx.coroutines.delay
import ru.topbun.android.utills.LocationAd.OTHER
import ru.topbun.android.utills.LocationAd.RU
import ru.topbun.android.utills.getLocation
import ru.topbun.android.utills.isShowAd
import ru.topbun.domain.entity.AdType
import ru.topbun.ui.BuildConfig
import kotlin.random.Random

@Composable
fun InterstitialAd(activity: Activity, isEnabledAd: Boolean, yandexId: String?, applovinId: String?, onAdLoaded: () -> Unit = {}) {
    if(isShowAd(AdType.INTER) && isEnabledAd){
        if (BuildConfig.RUSTORE){
            yandexId?.let {
                AppInterstitialAd.Yandex(yandexId).invoke(activity, onAdLoaded)
            }
        } else {
            val location = activity.applicationContext.getLocation()
            when(location){
                RU -> {
                    yandexId?.let {
                        AppInterstitialAd.Yandex(yandexId).invoke(activity, onAdLoaded)
                    }
                }
                OTHER -> {
                    applovinId?.let {
                        AppInterstitialAd.Applovin(applovinId).invoke(activity, onAdLoaded)
                    }
                }
            }
        }

    } else {
        onAdLoaded()
    }
}

sealed interface AppInterstitialAd{

    class Yandex(private val adId: String):AppInterstitialAd{

        @Composable
        operator fun invoke(activity: Activity, onAdLoaded: () -> Unit = {}){
            var interstitialAd: InterstitialAd? = null
            var interstitialAdLoader: InterstitialAdLoader? = null

            fun loadInterstitialAd() {
                val adRequestConfiguration = AdRequestConfiguration.Builder(adId).build()
                interstitialAdLoader?.loadAd(adRequestConfiguration)
            }

            interstitialAdLoader = InterstitialAdLoader(activity.applicationContext).apply {
                setAdLoadListener(object : InterstitialAdLoadListener {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d("YANDEX_INTER_AD", "Реклама загружена и готова к показу")
                        ad.show(activity)
                        onAdLoaded()
                    }

                    override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                        Log.d("YANDEX_INTER_AD", "При загрузке рекламы приходила ошибка: ${adRequestError}")
                        onAdLoaded()
                    }
                })
            }

            interstitialAd?.apply {
                setAdEventListener(object : InterstitialAdEventListener {

                    override fun onAdShown() {
                        Log.d("YANDEX_INTER_AD", "Реклама показана успешно")
                        onAdLoaded()
                    }
                    override fun onAdFailedToShow(adError: AdError) {
                        Log.d("YANDEX_INTER_AD", "Во время показа рекламы произошла ошибка: ${adError.description}")

                        interstitialAd?.setAdEventListener(null)
                        interstitialAd = null
                        onAdLoaded()

                    }
                    override fun onAdDismissed() {
                        interstitialAd?.setAdEventListener(null)
                        interstitialAd = null
                        Log.d("YANDEX_INTER_AD", "onAdDismissed()")
                        onAdLoaded()
                    }
                    override fun onAdClicked() {
                        Log.d("YANDEX_INTER_AD", "onAdClicked()")
                        onAdLoaded()
                    }
                    override fun onAdImpression(impressionData: ImpressionData?) {
                        Log.d("YANDEX_INTER_AD", "onAdImpression()")
                        onAdLoaded()
                    }
                })
                show(activity)
            }
            SideEffect {
                loadInterstitialAd()
            }
            DisposableEffect(Unit) {
                onDispose {
                    interstitialAdLoader?.setAdLoadListener(null)
                    interstitialAdLoader = null
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null
                }
            }
        }

    }

    class Applovin(private val adId: String): AppInterstitialAd{

        @Composable
        operator fun invoke(activity: Activity, onAdFinished: () -> Unit = {}) {
            val interstitialAd = MaxInterstitialAd(adId, activity)

            interstitialAd.setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd) {
                    if (interstitialAd.isReady) {
                        interstitialAd.showAd()
                        onAdFinished()
                    }
                }

                override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                    onAdFinished()
                }

                override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                    interstitialAd.loadAd()
                    onAdFinished()
                }

                override fun onAdDisplayed(ad: MaxAd) {
                    Log.d("APPLOVIN_INTER_AD", "Ad displayed")
                    onAdFinished()
                }

                override fun onAdClicked(ad: MaxAd) {
                    Log.d("APPLOVIN_INTER_AD", "Ad clicked")
                    onAdFinished()
                }

                override fun onAdHidden(ad: MaxAd) {
                    interstitialAd.loadAd()
                    onAdFinished()
                }
            })

            LaunchedEffect(Unit) {
                interstitialAd.loadAd()
                delay(5_000)
                if (!interstitialAd.isReady) onAdFinished()
            }

            DisposableEffect(Unit) {
                onDispose {
                    interstitialAd.setListener(null)
                }
            }
        }

    }

}
