package ru.topbun.data.di

import org.koin.dsl.module
import ru.topbun.data.database.AppDatabase
import ru.topbun.data.database.dao.FavoriteDao

internal val databaseModule = module {
    single<AppDatabase>{ AppDatabase.getInstance(context = get()) }
    single<FavoriteDao>{ get<AppDatabase>().favoriteDao() }
}