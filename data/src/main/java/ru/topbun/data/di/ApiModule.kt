package ru.topbun.data.di

import org.koin.dsl.module
import ru.topbun.data.api.ApiFactory
import ru.topbun.data.api.LocationApi
import ru.topbun.data.api.ModsApi

internal val apiModule = module {
    single<ModsApi>{ ApiFactory.modsApi }
    single<LocationApi>{ ApiFactory.locationApi }
}