package ru.topbun.splash.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.topbun.splash.SplashViewModel

val splashFeatureModule = module {
    viewModel { SplashViewModel(get(), get()) }
}