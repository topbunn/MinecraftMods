package ru.topbun.android.ads.natives

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import ru.topbun.android.ads.natives.ApplovinNativeAdManager
import ru.topbun.android.ads.natives.YandexNativeAdManager

@Composable
fun YandexNativeAdView(
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            FrameLayout(ctx).apply {
                val adView = YandexNativeAdManager.popAd()
                if (adView != null) addView(adView)
            }
        },
        modifier = modifier
    )
}
