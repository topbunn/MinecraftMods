package ru.topbun.detail_mod.dontWorkAddon

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.android.utills.isEmail
import ru.topbun.data.repository.ModRepository
import ru.topbun.domain.entity.IssueEntity

class DontWorkAddonViewModel(
    private val repository: ModRepository
) : ScreenModel {

    private val _state = MutableStateFlow(DontWorkAddonState())
    val state = _state.asStateFlow()

    private val _events = Channel<DontWorkAddonEvent>()
    val events = _events.receiveAsFlow()

    fun changeEmail(email: String) = _state.update { it.copy(email = email) }
    fun changeMessage(message: String) = _state.update { it.copy(message = message) }

    fun sendIssue() = screenModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val issue = IssueEntity(email = state.value.email, text = state.value.message)
        val result = repository.sendIssue(issue)
        result.onSuccess {
            _state.update { it.copy(email = "", message = "") }
            _events.send(DontWorkAddonEvent.ShowSuccess)
        }.onFailure {
            _events.send(DontWorkAddonEvent.ShowError(it.message ?: "Error"))
        }
        _state.update { it.copy(isLoading = false) }
    }

    val buttonEnabled = _state.map {
        it.email.isEmail() && it.message.length in (32..1024)
    }.stateIn(screenModelScope, SharingStarted.Eagerly, false)

}