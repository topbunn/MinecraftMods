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
import ru.topbun.data.repository.LocationRepository
import ru.topbun.data.repository.ModRepository

class SplashViewModel(
    private val application: Application,
    private val modRepository: ModRepository,
    private val locationRepository: LocationRepository
) : ScreenModel {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()


    private fun simulateLoading() = screenModelScope.launch {
        delay(10000)
        _state.update { it.copy(onOpenApp = true) }
    }

    fun navigateOnce() {
        _state.update { it.copy(navigated = true) }
    }

    private fun initAds() = screenModelScope.launch {
        val config = modRepository.getConfig()
        val location = locationRepository.getLocation()
        InterAdInitializer.init(application.applicationContext, location, config)
        NativeAdInitializer.init(application.applicationContext, location, config)
        _state.update { it.copy(adInit = true) }
    }

    init {
        initAds()
        simulateLoading()
    }

}