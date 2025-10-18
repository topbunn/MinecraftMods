package ru.topbun.apps

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.data.repository.ModRepository
import ru.topbun.domain.entity.app.AppInfoEntity

class AppsViewModel(application: Application): AndroidViewModel(application) {

    private val repository = ModRepository(application)

    private val _state = MutableStateFlow(AppsState())
    val state = _state.asStateFlow()

    init {
        loadApps()
    }

    fun openApp(app: AppInfoEntity){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.googlePlayLink))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

    fun loadApps() = viewModelScope.launch {
        _state.update { it.copy(screenState = AppsState.AppsStateScreen.Loading) }
        repository.getApps().onSuccess{ result ->
            _state.update {
                it.copy(
                    appsInfo = result,
                    screenState = AppsState.AppsStateScreen.Success
                )
            }
        }.onFailure {
            _state.update { it.copy(screenState = AppsState.AppsStateScreen.Error("Loading error"))}
        }
    }

}