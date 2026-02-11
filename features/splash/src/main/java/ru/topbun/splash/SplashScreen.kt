package ru.topbun.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            if (state.loadingIsEnd){
                viewModel.navigateToTabsScreen()
            }
        }

        LaunchedEffect(state.navigate) {
            state.navigate?.let {
                val screen = ScreenRegistry.get(it)
                navigator.replace(screen)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.BLACK_BG)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current
            val applicationName = context.applicationInfo.labelRes
            val logoAppRes = koinInject<LogoAppRes>()
            Text(
                text = stringResource(applicationName),
                style = Typography.APP_TEXT,
                fontSize = 32.sp,
                color = Colors.WHITE,
                fontFamily = Fonts.INTER.BOLD,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(30.dp))
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                painter = painterResource(logoAppRes.logoRes),
                contentDescription = "Image preview",
                contentScale = ContentScale.FillWidth
            )
            Spacer(Modifier.height(50.dp))
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
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }


}