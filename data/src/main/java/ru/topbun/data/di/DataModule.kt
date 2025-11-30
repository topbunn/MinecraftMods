package ru.topbun.data.di

import org.koin.dsl.module

val dataModule = module {
    includes(
        apiModule,
        databaseModule,
        mappersModule,
        repositoryModule,
        storageModule,

    )
}