package ru.topbun.main.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.topbun.main.MainViewModel

val mainFeatureModule = module {
    viewModel { MainViewModel(get()) }
}