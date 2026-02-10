package ru.topbun.android.ads.natives.yandex

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import ru.topbun.android.R

@Composable
fun YandexFullscreenAdView(modifier: Modifier) {
    val context = LocalContext.current
    val adView = remember { YandexNativeAdManager.popAd(context, R.layout.fullscreen_yandex) } ?: return

    key("native_ad") {
        AndroidView(
            modifier = modifier,
            factory = { adView }
        )
    }
}
