package ru.topbun.android.ads.natives.yandex

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ru.topbun.ui.theme.Fonts

@Composable
fun YandexBannerAdView(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isLoaded by remember { mutableStateOf(false) }

    val preloadedView = remember { YandexBannerAdPreloader.popAd(context) }

    if (preloadedView != null) {
        isLoaded = true
    }

    val manager = remember {
        if (preloadedView == null) {
            YandexBannerAdManager(adUnitId) { loaded ->
                isLoaded = loaded
            }
        } else null
    }

    val height = if (isLoaded) Modifier.wrapContentHeight() else Modifier.height(0.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(height),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                if (preloadedView != null) {
                    (preloadedView.parent as? FrameLayout)?.removeView(preloadedView)
                    preloadedView
                } else {
                    FrameLayout(ctx).also { container ->
                        manager?.attach(ctx, container)
                    }
                }
            }
        )
        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(0.4f), RoundedCornerShape(16.dp))
                .padding(8.dp, 2.dp),
            text = "Ad",
            color = Color.White,
            fontSize = 12.sp,
            fontFamily = Fonts.INTER.BOLD
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            if (preloadedView != null) {
                preloadedView.destroy()
                YandexBannerAdPreloader.preload(context)
            } else {
                manager?.destroy()
            }
        }
    }
}