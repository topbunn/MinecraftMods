package ru.topbun.splash

import ru.topbun.domain.entity.ConfigEntity
import ru.topbun.navigation.SharedScreen

data class SplashState(
    val loadingIsEnd: Boolean = false,
    val navigate: SharedScreen? = null
)
