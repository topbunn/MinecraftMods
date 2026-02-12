package ru.topbun.android.ads.natives.applovin

import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.applovin.mediation.nativeAds.MaxNativeAdView
import ru.topbun.android.R

@Composable
fun ApplovinFullscreenAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var adHolder by remember { mutableStateOf<ApplovinNativeAdManager.NativeAdHolder?>(null) }

    DisposableEffect(Unit) {
        adHolder = ApplovinNativeAdManager.popAd(
            context,
            R.layout.fullscreen_applovin
        )

        onDispose {
            adHolder?.let { holder ->
                ApplovinNativeAdManager.destroyAd(holder.ad)
            }
        }
    }

    adHolder?.let { holder ->
        AndroidView(
            factory = { holder.view },
            modifier = modifier
        )
    }
}