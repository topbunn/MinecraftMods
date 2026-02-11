package ru.topbun.feedback

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.android.utills.isEmail
import ru.topbun.data.repository.ModRepository
import ru.topbun.domain.entity.IssueEntity

class FeedbackViewModel(
    private val repository: ModRepository
): ScreenModel {

    private val _state = MutableStateFlow(FeedbackState())
    val state get() = _state.asStateFlow()

    private val _events = Channel<FeedbackEvent>()
    val events get() = _events.receiveAsFlow()

    fun changeEmail(value: String) = _state.update { it.copy(email = value) }
    fun changeMessage(value: String) = _state.update { it.copy(message = value) }

    fun sendFeedback() = screenModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val issue = IssueEntity(
            email = _state.value.email,
            text = _state.value.message,
        )
        repository.sendIssue(issue).onSuccess {
            _events.send(FeedbackEvent.ShowSuccess)
            _state.update { it.copy(email = "", message = "") }
        }.onFailure {
            _events.send(FeedbackEvent.ShowError)
        }

        _state.update { it.copy(isLoading = false) }
    }


    fun handleChangeState() {
        _state
            .map { listOf(it.email, it.message) }
            .distinctUntilChanged()
            .drop(1)
            .onEach {
                _state.update { it.copy(sendIsValid = checkSubmitValid()) }
            }.launchIn(screenModelScope)
    }

    private fun checkSubmitValid() = with(_state.value) {
        email.isEmail() && message.length >= 32
    }


}