package ru.topbun.splash.di

import org.koin.dsl.module
import ru.topbun.splash.SplashViewModel

val splashFeatureModule = module {
    factory { SplashViewModel(get(), get()) }
}