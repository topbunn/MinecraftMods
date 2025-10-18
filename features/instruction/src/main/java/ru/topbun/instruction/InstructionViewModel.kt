package ru.topbun.instruction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.data.repository.ModRepository

class InstructionViewModel(application: Application): AndroidViewModel(application) {

    private val repository = ModRepository(application)

    private val _state = MutableStateFlow(InstructionState())
    val state = _state.asStateFlow()

    init {
        loadConfig()
    }

    private fun loadConfig() = viewModelScope.launch {
        val config = repository.getConfig()
        _state.update { it.copy(config = config) }
    }

}