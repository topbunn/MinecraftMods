package ru.topbun.android.ads.natives

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ApplovinNativeAdView(
    context: Context,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { ctx ->
            FrameLayout(ctx).apply {
                val adView = ApplovinNativeAdManager.popAd(context)
                if (adView != null) addView(adView)
            }
        },
        modifier = modifier
    )
}
