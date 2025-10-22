package ru.topbun.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.data.repository.ModRepository

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ModRepository(application)

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    private fun loadConfig() = viewModelScope.launch {
        val config = repository.getConfig()
        _state.update { it.copy(config = config) }
    }

    private fun simulateLoading() = viewModelScope.launch{
        delay(8500)
        _state.update { it.copy(onOpenApp = true) }
    }

    init {
        loadConfig()
        simulateLoading()
    }

}