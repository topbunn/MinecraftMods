package ru.topbun.favorite

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.data.database.entity.FavoriteEntity
import ru.topbun.data.repository.ModRepository
import ru.topbun.domain.entity.mod.ModEntity
import ru.topbun.favorite.FavoriteState.FavoriteScreenState

class FavoriteViewModel(
    private val repository: ModRepository
): ScreenModel {

    private val _state = MutableStateFlow(FavoriteState())
    val state = _state.asStateFlow()


    fun changeFavorite(mod: ModEntity) = screenModelScope.launch{
        val favorite = FavoriteEntity(modId = mod.id, status = false)
        repository.addFavorite(favorite)
        _state.update {
            val newMods = it.mods.toMutableList()
            newMods.removeIf { it.id == mod.id }
            it.copy(mods = newMods)
        }
        getFavoriteSize()
    }

    fun resetMods(){
        _state.update { FavoriteState() }
        getFavoriteSize()
    }

    fun changeOpenMod(mod: ModEntity?) = _state.update { it.copy(openMod = mod) }

    fun loadMods() = screenModelScope.launch{
        _state.update { it.copy(favoriteScreenState = FavoriteScreenState.Loading) }
        val result = repository.getFavoriteMods(
            offset = _state.value.mods.size
        )
        result.onSuccess { mods ->
            _state.update {
                it.copy(
                    mods = it.mods + mods,
                    isEndList = mods.isEmpty(),
                    favoriteScreenState = FavoriteScreenState.Success
                )
            }
        }.onFailure { error ->
            _state.update { it.copy(favoriteScreenState = FavoriteScreenState.Error(error.message ?: "Loading error")) }
        }

    }

    private fun getFavoriteSize() = screenModelScope.launch {
        val size = repository.getFavoriteSize()
        _state.update { it.copy(favoriteSize = size) }
    }

}