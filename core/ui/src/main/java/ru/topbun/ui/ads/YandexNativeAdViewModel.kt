package ru.topbun.ui.ads

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.topbun.ui.BuildConfig

class YandexNativeAdViewModel(application: Application, private val adId: String) : AndroidViewModel(application) {

    private var nativeAdLoader: NativeAdLoader? = null
    private val _nativeAd = MutableStateFlow<NativeAd?>(null)
    val nativeAd: StateFlow<NativeAd?> = _nativeAd

    init {
        loadAd()
    }

    private fun loadAd() {
        nativeAdLoader = NativeAdLoader(application).apply {
            setNativeAdLoadListener(object : NativeAdLoadListener {
                override fun onAdLoaded(ad: NativeAd) {
                    _nativeAd.value = ad
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    Log.e("NativeAd", "Failed: ${error.description}")
                }
            })
        }

        val request = NativeAdRequestConfiguration.Builder(adId)
            .build()

        nativeAdLoader?.loadAd(request)
    }

    override fun onCleared() {
        nativeAdLoader = null
        _nativeAd.value = null
        super.onCleared()
    }
}