package ru.topbun.android.utills

import android.util.Log
import org.koin.java.KoinJavaComponent.inject
import org.koin.mp.KoinPlatform.getKoin
import ru.topbun.android.BuildConfig
import ru.topbun.domain.entity.AdType
import ru.topbun.domain.entity.AdType.*
import ru.topbun.domain.entity.modConfig.ModConfigProvider
import kotlin.random.Random

fun isShowAd(type: AdType): Boolean {
    val configProvider: ModConfigProvider by inject(ModConfigProvider::class.java)
    val config = configProvider.getConfig()
    val percent = when (type) {
        NATIVE -> config.percentShowNativeAd
        INTER -> config.percentShowInterAd
    }

    val randomValue = Random.nextInt(100)
    val isShowAd = randomValue < percent

    Log.d("CHANCE_SHOW_AD", "$type $percent% -> $isShowAd")

    return isShowAd
}