package ru.topbun.favorite.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.topbun.favorite.FavoriteViewModel

val favoriteFeatureModule = module {
    viewModel { FavoriteViewModel(get()) }
}