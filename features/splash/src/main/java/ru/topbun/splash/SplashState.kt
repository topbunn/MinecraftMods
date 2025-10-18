package ru.topbun.splash

import ru.topbun.domain.entity.ConfigEntity

data class SplashState(
    val onOpenApp: Boolean = false,
    val config: ConfigEntity? = null,
)
