package ru.topbun.data.di

import org.koin.dsl.module
import ru.topbun.data.repository.ModRepository

internal val repositoryModule = module {
    single {
        ModRepository(
            favoriteDao = get(),
            api = get(),
            modMapper = get(),
            dataStore = get(),
            configProvider = get()
        )
    }
}