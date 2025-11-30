package ru.topbun.data.di

import org.koin.dsl.module
import ru.topbun.data.api.ApiFactory
import ru.topbun.data.api.ApiService

internal val apiModule = module {
    single<ApiService>{ ApiFactory.api }
}