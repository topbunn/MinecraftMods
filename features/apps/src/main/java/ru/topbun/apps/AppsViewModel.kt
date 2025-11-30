package ru.topbun.apps

import android.app.Application
import android.content.Intent
import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.data.repository.ModRepository
import ru.topbun.domain.entity.app.AppInfoEntity

class AppsViewModel(
    private val application: Application,
    private val repository: ModRepository
): ScreenModel {


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

    fun loadApps() = screenModelScope.launch {
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