package ru.topbun.detail_mod

import ru.topbun.domain.entity.ConfigEntity
import ru.topbun.domain.entity.mod.ModEntity

data class DetailModState(
    val mod: ModEntity? = null,
    val choiceFilePathSetup: String? = null,
    val dontWorkAddonDialogIsOpen: Boolean = false,
    val descriptionImageExpand: Boolean = false,
    val descriptionTextExpand: Boolean = false,
    val loadModState: LoadModState = LoadModState.Idle,
    val downloadState: DownloadModState = DownloadModState.Idle,
    val config: ConfigEntity? = null
){

    sealed interface LoadModState{

        data object Idle: LoadModState
        data object Loading: LoadModState
        data class Error(val message: String): LoadModState
        data object Success: LoadModState

    }

    sealed interface DownloadModState{

        data object Idle: DownloadModState
        data class Loading(val progress: Int): DownloadModState
        data class Error(val message: String): DownloadModState
        data object Success: DownloadModState

    }

}
