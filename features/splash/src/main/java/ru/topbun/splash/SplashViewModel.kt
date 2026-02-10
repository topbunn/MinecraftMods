package ru.topbun.splash

import android.app.Application
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.android.ads.inter.InterAdInitializer
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.android.ads.natives.NativeAdInitializer.PreloadType.ERROR
import ru.topbun.android.ads.natives.NativeAdInitializer.PreloadType.SUCCESS
import ru.topbun.data.repository.LocationRepository
import ru.topbun.data.repository.ModRepository
import ru.topbun.navigation.SharedScreen
import java.lang.annotation.Native

class SplashViewModel(
    private val application: Application,
    private val modRepository: ModRepository,
    private val locationRepository: LocationRepository
) : ScreenModel {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()


    private fun forcedNavigate() = screenModelScope.launch {
        delay(10000)
        _state.update { it.copy(loadingIsEnd = true) }
    }

    fun navigateToTabsScreen() = screenModelScope.launch{
        val tabsScreen = SharedScreen.TabsScreen
        val screen = if (NativeAdInitializer.hasNativeAd()) SharedScreen.FullscreenAdScreen(tabsScreen) else tabsScreen
        _state.update { it.copy(navigate = screen) }
    }


    private fun initAds() = screenModelScope.launch {
        val config = modRepository.getConfig()
        val location = locationRepository.getLocation()
        NativeAdInitializer.init(application.applicationContext, location, config)
        NativeAdInitializer.setOnListener {
            when(it){
                ERROR, SUCCESS -> {
                    _state.update { it.copy(loadingIsEnd = true) }
                    NativeAdInitializer.deleteListener()
                }
                else -> {}
            }
        }
    }


    init {
        initAds()
        forcedNavigate()
    }

}