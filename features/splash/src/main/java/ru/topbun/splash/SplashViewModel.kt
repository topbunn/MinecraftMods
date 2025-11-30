package ru.topbun.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.android.ads.inter.InterAdInitializer
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.data.repository.ModRepository

class SplashViewModel(
    private val application: Application,
    private val repository: ModRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()


    private fun simulateLoading() = viewModelScope.launch {
        delay(10000)
        _state.update { it.copy(onOpenApp = true) }
    }

    fun navigateOnce() {
        _state.update { it.copy(navigated = true) }
    }

    private fun initAds() = viewModelScope.launch {
        val config = repository.getConfig()
        InterAdInitializer.init(application.applicationContext, config)
        NativeAdInitializer.init(application.applicationContext, config)
        _state.update { it.copy(adInit = true) }
    }

    init {
        initAds()
        simulateLoading()
    }

}