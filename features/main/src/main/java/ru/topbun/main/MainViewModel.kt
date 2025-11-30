package ru.topbun.main

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.data.database.entity.FavoriteEntity
import ru.topbun.data.repository.ModRepository
import ru.topbun.domain.entity.mod.ModEntity
import ru.topbun.main.MainState.MainScreenState

class MainViewModel(
    private val repository: ModRepository
) : ScreenModel  {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private var loadModsJob: Job? = null

    fun changeOpenMod(mod: ModEntity?) = _state.update { it.copy(openMod = mod) }

    fun changeFavorite(mod: ModEntity) = screenModelScope.launch{
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

    fun changeSearch(value: String) = _state.update { it.copy(search = value) }
    fun changeModSort(selectedIndex: Int) = _state.update { it.copy(modSortSelectedIndex = selectedIndex) }
    fun changeSortType(modTypeUi: ModTypeUi) = _state.update { it.copy(selectedModTypeUi = modTypeUi) }


    fun handleChangeState() {
        _state
            .map { listOf(it.search, it.modSortSelectedIndex, it.selectedModTypeUi) }
            .distinctUntilChanged()
            .drop(1)
            .onEach {
                refreshMods()
            }
            .launchIn(screenModelScope)
    }

    fun refreshMods() {
        _state.update {
            it.copy(mods = emptyList(), mainScreenState = MainScreenState.Idle, isEndList = false)
        }
    }

    fun loadMods(){
        loadModsJob?.cancel()
        loadModsJob = screenModelScope.launch{
            _state.update { it.copy(mainScreenState = MainScreenState.Loading) }
            val result = repository.getMods(
                q = _state.value.search,
                offset = _state.value.mods.size,
                type = _state.value.selectedModTypeUi.toModSortType(),
                sortType = _state.value.modSorts[state.value.modSortSelectedIndex].toModSortType()
            )
            result.onSuccess { mods ->
                _state.update {
                    it.copy(
                        mods = it.mods + mods,
                        isEndList = mods.isEmpty(),
                        mainScreenState = MainScreenState.Success
                    )
                }
            }.onFailure { error ->
                error.printStackTrace()
                _state.update { it.copy(mainScreenState = MainScreenState.Error("Loading error. Check Internet connection")) }
            }
        }
    }

}