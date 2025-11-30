package ru.topbun.main.di

import org.koin.dsl.module
import ru.topbun.main.MainViewModel

val mainFeatureModule = module {
    single { MainViewModel(get()) }
}