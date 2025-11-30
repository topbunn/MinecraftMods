package ru.topbun.apps.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.topbun.apps.AppsViewModel

val appsFeatureModule = module {
    single { AppsViewModel(get(), get()) }
}