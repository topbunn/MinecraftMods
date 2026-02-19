package ru.topbun.android.ads.banner.yandex

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ru.topbun.ui.theme.Fonts

@Composable
fun YandexBannerAdView(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    var isLoaded by remember { mutableStateOf(false) }

    val manager = remember {
        YandexBannerAdManager(adUnitId) { loaded ->
            isLoaded = loaded
        }
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
                FrameLayout(ctx).also { container ->
                    manager.attach(ctx, container)
                }
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            manager.destroy()
        }
    }
}
