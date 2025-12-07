package ru.topbun.android.ads.natives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ApplovinNativeAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val adView = remember { ApplovinNativeAdManager.popAd(context) } ?: return

    key("native_ad") {
        AndroidView(
            factory = { adView },
            modifier = modifier
        )
    }
}
