package ru.topbun.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.data.database.entity.FavoriteEntity
import ru.topbun.data.repository.ModRepository
import ru.topbun.domain.entity.mod.ModEntity
import ru.topbun.main.MainState.MainScreenState
import kotlin.collections.map

class MainViewModel(application: Application) : AndroidViewModel(application)  {

    private val repository = ModRepository(application)

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private var loadModsJob: Job? = null

    fun changeOpenMod(mod: ModEntity?) = _state.update { it.copy(openMod = mod) }

    fun changeFavorite(mod: ModEntity) = viewModelScope.launch{
        val favorite = FavoriteEntity(
            modId = mod.id,
            status = !mod.isFavorite
        )
        repository.addFavorite(favorite)
        val newMods = _state.value.mods.map {
            if (it.id == favorite.modId) it.copy(isFavorite = favorite.status) else it
        }
        _state.update {
            it.copy(mods = newMods)
        }
    }

    fun changeSearch(value: String) = _state.update { it.copy(search = value) }.also { clearAndLoadMods() }
    fun changeModSort(selectedIndex: Int) = _state.update { it.copy(modSortSelectedIndex = selectedIndex) }.also { clearAndLoadMods() }
    fun changeSortType(modTypeUi: ModTypeUi) = _state.update { it.copy(selectedModTypeUi = modTypeUi) }.also { clearAndLoadMods() }

    private fun clearAndLoadMods(){
        _state.update { it.copy(mods = emptyList()) }
        loadMods()
    }

    fun loadMods(){
        loadModsJob?.cancel()
        loadModsJob = viewModelScope.launch{
            _state.update { it.copy(mainScreenState = MainScreenState.Loading) }
            val result = repository.getMods(
                q = _state.value.search,
                offset = _state.value.mods.size,
                type = _state.value.selectedModTypeUi.toModSortType(),
                sortType = _state.value.modSorts[state.value.modSortSelectedIndex].toModSortType()
            )
            result.onSuccess { mods ->
                _state.update { it.copy(mods = it.mods + mods, mainScreenState = MainScreenState.Success) }
            }.onFailure { error ->
                error.printStackTrace()
                _state.update { it.copy(mainScreenState = MainScreenState.Error("Loading error. Check Internet connection")) }
            }
        }
    }

    init {
        loadConfig()
    }

    private fun loadConfig() = viewModelScope.launch {
        val config = repository.getConfig()
        _state.update { it.copy(config = config) }
    }


}