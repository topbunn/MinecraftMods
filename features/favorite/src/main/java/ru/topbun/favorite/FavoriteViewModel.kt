package ru.topbun.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
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
): ViewModel() {

    private val _state = MutableStateFlow(FavoriteState())
    val state = _state.asStateFlow()


    fun removeFavorite(mod: ModEntity) = viewModelScope.launch{
        val favorite = FavoriteEntity(modId = mod.id, status = false)
        repository.addFavorite(favorite)
        _state.update {
            val newMods = it.mods.toMutableList()
            newMods.removeIf { it.id == mod.id }
            it.copy(mods = newMods)
        }
    }

    fun openMod(mod: ModEntity?) = _state.update { it.copy(openMod = mod) }

    fun loadMods() = viewModelScope.launch{
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

}