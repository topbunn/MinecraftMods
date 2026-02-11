package ru.topbun.android.ads.natives.applovin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ru.topbun.android.R
import ru.topbun.ui.theme.Colors

@Composable
fun ApplovinContentAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val adView = remember { ApplovinNativeAdManager.popAd(context, R.layout.native_applovin) } ?: return

    key("native_ad") {
        AndroidView(
            factory = { adView },
            modifier = modifier
        )
    }
}
