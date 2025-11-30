package ru.topbun.detail_mod.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.topbun.detail_mod.DetailModViewModel
import ru.topbun.detail_mod.dontWorkAddon.DontWorkAddonViewModel

val detailModFeatureModule = module {
    factory{ (modId: Int) -> DetailModViewModel(modId, get()) }
    factory {  DontWorkAddonViewModel(get()) }
}