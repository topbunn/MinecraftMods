package com.youlovehamit.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.topbun.android.ads.inter.ApplovinInterAdManager
import ru.topbun.android.ads.inter.InterAdInitializer
import ru.topbun.android.ads.inter.YandexInterAdManager
import ru.topbun.android.ads.natives.ApplovinNativeAdManager
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.android.ads.open.OpenAdInitializer
import ru.topbun.data.repository.ModRepository
import ru.topbun.ui.App
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.colorScheme
import ru.topbun.ui.utils.requestPermissions

class MainActivity : ComponentActivity() {

    private val repository by lazy { ModRepository(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initOpenAd()
        enableEdgeToEdge()
        setContent {
            requestPermissions(Manifest.permission.POST_NOTIFICATIONS)
            MaterialTheme(colorScheme.copy(primary = Colors.PRIMARY)) {
                App()
            }
        }
    }

    private fun initOpenAd() = lifecycleScope.launch{
        val config = repository.getConfig()
        OpenAdInitializer.init(this@MainActivity, config)
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

    }


}