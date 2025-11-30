package ru.topbun.favorite

import ru.topbun.domain.entity.mod.ModEntity

data class FavoriteState(
    val mods: List<ModEntity> = emptyList(),
    val favoriteSize: Int? = null,
    val openMod: ModEntity? = null,
    val isEndList: Boolean = false,
    val favoriteScreenState: FavoriteScreenState = FavoriteScreenState.Idle,
){

    sealed interface FavoriteScreenState{

        object Idle: FavoriteScreenState
        object Loading: FavoriteScreenState
        object Success: FavoriteScreenState
        data class Error(val message: String): FavoriteScreenState

    }

}
