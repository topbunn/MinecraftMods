package com.youlovehamit.app.app

import android.app.Application

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        initKoin()
        initSharedScreens()
        initAppLovin()
        initYandex()
        initFirebasePush()
        connectMetrics()
    }

}