package ru.topbun.data.di

import org.koin.dsl.module
import ru.topbun.data.storage.DataStoreStorage

internal val storageModule = module {
    single { DataStoreStorage(get()) }
}