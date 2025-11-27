package ru.topbun.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.topbun.data.database.entity.FavoriteEntity
import ru.topbun.data.repository.ModRepository
import ru.topbun.domain.entity.mod.ModEntity
import ru.topbun.favorite.FavoriteState.FavoriteScreenState

class FavoriteViewModel(application: Application): AndroidViewModel(application) {

    private val repository = ModRepository(application)

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
        _state.update { it.copy(favoriteScreenState = FavoriteScreenState.Loading, mods = emptyList()) }
        val result = repository.getFavoriteMods()
        result.onSuccess { mods ->
            _state.update {
                it.copy(
                    mods = _state.value.mods + mods,
                    favoriteScreenState = FavoriteScreenState.Success
                )
            }
        }.onFailure { error ->
            _state.update { it.copy(favoriteScreenState = FavoriteScreenState.Error(error.message ?: "Loading error")) }
        }

    }

}