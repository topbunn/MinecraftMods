package ru.topbun.android.ads.natives

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ru.topbun.android.ads.natives.ApplovinNativeAdManager
import ru.topbun.android.ads.natives.YandexNativeAdManager

@Composable
fun YandexNativeAdView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val adView = remember {
        FrameLayout(context).apply {
            val view = YandexNativeAdManager.popAd()
            if (view != null) addView(view)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { adView }
    )
}
