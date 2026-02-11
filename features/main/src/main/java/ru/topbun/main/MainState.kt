package ru.topbun.main

import androidx.compose.foundation.lazy.LazyListState
import ru.topbun.domain.entity.ConfigEntity
import ru.topbun.domain.entity.mod.ModEntity

data class MainState(
    val mods: List<ModEntity> = emptyList(),
    val openMod: ModEntity? = null,
    val search: String = "",
    val modListState: LazyListState = LazyListState(),
    val modSorts: List<ModSortTypeUi> = ModSortTypeUi.entries,
    val modSortSelectedIndex: Int = 0,
    val modTypeUis: List<ModTypeUi> = ModTypeUi.entries,
    val selectedModTypeUi: ModTypeUi = ModTypeUi.ALL,
    val isEndList: Boolean = false,
    val mainScreenState: MainScreenState = MainScreenState.Idle
){

    sealed interface MainScreenState{

        object Idle: MainScreenState
        object Loading: MainScreenState
        object Success: MainScreenState
        object Error: MainScreenState

    }

}
