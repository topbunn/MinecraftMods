package ru.topbun.android.ads.natives

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ApplovinNativeAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val adView = remember {
        FrameLayout(context).apply {
            val view = ApplovinNativeAdManager.popAd(context)
            if (view != null) addView(view)
        }
    }

    AndroidView(
        factory = { adView },
        modifier = modifier
    )
}