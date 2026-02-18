package com.youlovehamit.app

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import com.applovin.sdk.AppLovinSdk
import com.ironsource.adqualitysdk.sdk.i.it
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
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
        showApplovinConsentFlow()
        modRepository = getKoin().get()
        locationRepository = getKoin().get()
        initOpenAd()
        initNativeAd()
        initInterAd()
        enableEdgeToEdge()
        setContent {
            requestPermissions(Manifest.permission.POST_NOTIFICATIONS)
            MaterialTheme(colorScheme.copy(primary = Colors.PRIMARY)) {
                App()
            }
        }
    }

    private fun initNativeAd() = lifecycleScope.launch{
        val config = modRepository.getConfig()
        val location = locationRepository.getLocation()
        NativeAdInitializer.init(this@MainActivity, location, config)
    }

    private fun initInterAd() = lifecycleScope.launch{
        val config = modRepository.getConfig()
        val location = locationRepository.getLocation()
        InterAdInitializer.init(this@MainActivity.application, location, config)
    }

    private fun initOpenAd() = lifecycleScope.launch{
        val config = modRepository.getConfig()
        val location = locationRepository.getLocation()
        OpenAdInitializer.init(this@MainActivity, location, config)
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

    private fun showApplovinConsentFlow() {
        val cmpService = AppLovinSdk.getInstance(this).cmpService
        cmpService.showCmpForExistingUser(this) { error ->
            Log.d("CMP_SERVICE", error?.message ?: "отсутсвует")
        }
    }


}