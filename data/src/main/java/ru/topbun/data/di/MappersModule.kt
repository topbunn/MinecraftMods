package ru.topbun.data.di

import org.koin.dsl.module
import ru.topbun.data.mappers.ModMapper

internal val mappersModule = module {
    factory<ModMapper> { ModMapper(context = get()) }
}