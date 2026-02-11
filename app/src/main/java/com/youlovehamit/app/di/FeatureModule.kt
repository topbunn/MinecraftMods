package com.youlovehamit.app.di

import org.koin.dsl.module
import ru.topbun.apps.di.appsFeatureModule
import ru.topbun.detail_mod.di.detailModFeatureModule
import ru.topbun.favorite.di.favoriteFeatureModule
import ru.topbun.feedback.di.feedbackFeatureModule
import ru.topbun.main.di.mainFeatureModule
import ru.topbun.splash.di.splashFeatureModule

val featureModule = module {
    includes(
        appsFeatureModule,
        detailModFeatureModule,
        favoriteFeatureModule,
        feedbackFeatureModule,
        mainFeatureModule,
        splashFeatureModule,
    )
}