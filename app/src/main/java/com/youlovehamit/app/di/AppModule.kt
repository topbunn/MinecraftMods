package com.youlovehamit.app.di

import com.youlovehamit.app.AppModConfigProvider
import com.youlovehamit.app.R
import org.koin.dsl.module
import ru.topbun.domain.entity.LogoAppRes
import ru.topbun.domain.entity.modConfig.ModConfigProvider

val appModule = module {
    single<ModConfigProvider> { AppModConfigProvider() }
    single<LogoAppRes> { LogoAppRes(R.drawable.logo) }
}