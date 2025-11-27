package com.youlovehamit.app.app

import com.youlovehamit.app.di.appModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
