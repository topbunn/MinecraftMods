package ru.topbun.ui.ads

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.AndroidViewModel
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import ru.topbun.ui.BuildConfig
import ru.topbun.ui.R

class ApplovinNativeAdViewModel(application: Application, private val adId: String) : AndroidViewModel(application), MaxAdRevenueListener {

    var nativeAdContainerView: ViewGroup? = null
    private var nativeAdLoader: MaxNativeAdLoader? = null
    private var loadedNativeAd: MaxAd? = null

    private fun createNativeAdView(context: Context): MaxNativeAdView {
        val binder: MaxNativeAdViewBinder =
            MaxNativeAdViewBinder.Builder(R.layout.applovin_native_ad_container)
                .setTitleTextViewId(R.id.applovin_title)
                .setBodyTextViewId(R.id.applovin_body)
                .setAdvertiserTextViewId(R.id.applovin_advertiser)
                .setIconImageViewId(R.id.applovin_icon)
                .setMediaContentViewGroupId(R.id.applovin_media)
                .setOptionsContentViewGroupId(R.id.applovin_options)
                .setCallToActionButtonId(R.id.applovin_call_to_action)
                .build()
        return MaxNativeAdView(binder, context)
    }

    fun createNativeAdLoader()
    {
        nativeAdLoader = MaxNativeAdLoader(adId)
        nativeAdLoader?.setRevenueListener(this)
        nativeAdLoader?.setNativeAdListener(NativeAdListener())
    }

    fun loadNativeAd()
    {
        nativeAdLoader?.loadAd(createNativeAdView(getApplication()))
    }

    override fun onAdRevenuePaid(ad: MaxAd) {}

    private inner class NativeAdListener : MaxNativeAdListener()
    {
        override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd)
        {
            if (loadedNativeAd != null)
            {
                nativeAdLoader?.destroy(loadedNativeAd)
            }

            loadedNativeAd = nativeAd
            nativeAdContainerView?.removeAllViews()
            nativeAdContainerView?.addView(nativeAdView)
        }

        override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
            Log.e("APPLOVIN", error.toString())
            Log.e("APPLOVIN", error.waterfall.toString())
        }

        override fun onNativeAdClicked(nativeAd: MaxAd) {}


    }

    override fun onCleared() {
        super.onCleared()
        if (loadedNativeAd != null)
        {
            nativeAdLoader?.destroy(loadedNativeAd)
        }

        nativeAdLoader?.destroy()
    }
}