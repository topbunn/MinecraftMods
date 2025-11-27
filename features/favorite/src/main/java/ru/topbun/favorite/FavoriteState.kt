package ru.topbun.favorite

import ru.topbun.domain.entity.ConfigEntity
import ru.topbun.domain.entity.mod.ModEntity

data class FavoriteState(
    val mods: List<ModEntity> = emptyList(),
    val openMod: ModEntity? = null,
    val favoriteScreenState: FavoriteScreenState = FavoriteScreenState.Idle,
){

    sealed interface FavoriteScreenState{

        object Idle: FavoriteScreenState
        object Loading: FavoriteScreenState
        object Success: FavoriteScreenState
        data class Error(val message: String): FavoriteScreenState

    }

}
