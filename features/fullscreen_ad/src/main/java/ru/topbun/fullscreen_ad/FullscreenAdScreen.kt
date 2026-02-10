package ru.topbun.fullscreen_ad

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.parcelize.Parcelize
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.android.ads.natives.NativeAdInitializer.AdMode
import ru.topbun.navigation.SharedScreen

@Parcelize
data class FullscreenAdScreen(val screen: SharedScreen): Screen, Parcelable {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screen = rememberScreen(screen)
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xff0F1115))
                .systemBarsPadding()
        ){
            NativeAdInitializer.show(
                modifier = Modifier.fillMaxSize(),
                mode = AdMode.Fullscreen
            )
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 24.dp)
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(0.6f))
                    .clickable {
                        navigator.replace(screen)
                    }
                    .padding(6.dp),
                tint = Color.White,
                painter = painterResource(ru.topbun.ui.R.drawable.ic_exit),
                contentDescription = null
            )
        }
    }

}