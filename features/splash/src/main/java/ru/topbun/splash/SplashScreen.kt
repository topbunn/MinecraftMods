package ru.topbun.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import ru.topbun.domain.entity.LogoAppRes
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography

object SplashScreen : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<SplashViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(state.loadingIsEnd) {
            if (state.loadingIsEnd) {
                viewModel.navigateToTabsScreen()
            }
        }

        LaunchedEffect(state.navigate) {
            state.navigate?.let {
                val screen = ScreenRegistry.get(it)
                navigator.replace(screen)
            }
        }
        val scale = remember {
            Animatable(0f)
        }
        LaunchedEffect(key1 = true ){
            scale.animateTo(1f, animationSpec = tween(1000))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Colors.BLACK_BG,
                            Colors.PRIMARY,
                        )
                    )
                )
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current
            val applicationName = context.applicationInfo.labelRes
            val logoAppRes = koinInject<LogoAppRes>()
            Text(
                text = stringResource(applicationName),
                style = Typography.APP_TEXT.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Colors.WHITE, Colors.GRAY)
                    )
                ),
                fontSize = 40.sp,
                fontFamily = Fonts.INTER.BOLD,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(50.dp))
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale.value)
                    .clip(RoundedCornerShape(8.dp)),
                painter = painterResource(logoAppRes.logoRes),
                contentDescription = "Image preview",
                contentScale = ContentScale.FillWidth
            )
            Spacer(Modifier.height(100.dp))
            ProgressBar()
        }
    }

    @Composable
    fun ProgressBar() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                color = Colors.WHITE,
                trackColor = Colors.PRIMARY,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(ru.topbun.ui.R.string.loading),
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }


}