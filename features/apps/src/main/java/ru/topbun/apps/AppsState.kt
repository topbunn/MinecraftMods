package ru.topbun.apps

import ru.topbun.domain.entity.app.AppInfoEntity

data class AppsState(
    val appsInfo: List<AppInfoEntity> = emptyList(),
    val screenState: AppsStateScreen = AppsStateScreen.Idle
){

    sealed interface AppsStateScreen{
        object Idle: AppsStateScreen
        object Loading: AppsStateScreen
        data class Error(val message: String): AppsStateScreen
        object Success: AppsStateScreen

    }

}
