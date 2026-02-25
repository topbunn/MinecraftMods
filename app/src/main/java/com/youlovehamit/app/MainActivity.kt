package com.youlovehamit.app

import android.Manifest
import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import com.applovin.sdk.AppLovinSdk
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.datatransport.runtime.scheduling.SchedulingConfigModule_ConfigFactory
import com.google.android.datatransport.runtime.scheduling.SchedulingConfigModule_ConfigFactory.config
import com.youlovehamit.app.app.initAppLovin
import ru.topbun.android.ads.banner.BannerAdInitializer
import ru.topbun.android.ads.inter.InterAdInitializer
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.android.ads.open.OpenAdInitializer
import ru.topbun.data.repository.LocationRepository
import ru.topbun.data.repository.ModRepository
import ru.topbun.ui.App
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.colorScheme
import ru.topbun.ui.utils.requestPermissions

class MainActivity : ComponentActivity() {

    private lateinit var modRepository: ModRepository
    private lateinit var locationRepository: LocationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(TRANSPARENT, TRANSPARENT){ true },
            navigationBarStyle = SystemBarStyle.auto(TRANSPARENT, TRANSPARENT){ true },
        )

        initAppLovin(this@MainActivity){ initAds() }

        setContent {
            requestPermissions(Manifest.permission.POST_NOTIFICATIONS)
            MaterialTheme(colorScheme.copy(primary = Colors.PRIMARY)) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .background(Colors.BLACK_BG)
                        .navigationBarsPadding()
                ) {
                    Box(Modifier.weight(1f)) { App() }
//                    BannerAdInitializer.Show()
                }
            }
        }
    }


    private fun initAds() = lifecycleScope.launch {
        modRepository = getKoin().get()
        locationRepository = getKoin().get()
        val config = modRepository.getConfig()
        val location = locationRepository.getLocation()
        launch { InterAdInitializer.init(this@MainActivity.application, location, config) }
        launch { NativeAdInitializer.init(this@MainActivity.application, location, config) }
//        launch { BannerAdInitializer.init(location, config) }
        launch { OpenAdInitializer.init(this@MainActivity, location, config) }
    }

    override fun onStart() {
        super.onStart()
        OpenAdInitializer.onStart(this)
        InterAdInitializer.onStart()
    }

    override fun onStop() {
        super.onStop()
        OpenAdInitializer.onStop()
        InterAdInitializer.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        OpenAdInitializer.onDestroy()
        InterAdInitializer.onDestroy()
        NativeAdInitializer.onDestroy()
//        BannerAdInitializer.onDestroy()
    }


}