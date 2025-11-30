package com.youlovehamit.app.app

import com.youlovehamit.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

fun App.initKoin() {
    startKoin {
        androidLogger()
        androidContext(this@initKoin)
        modules(appModule)
    }
}
