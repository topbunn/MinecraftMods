package com.youlovehamit.app.app

import cafe.adriel.voyager.core.registry.ScreenRegistry
import ru.topbun.apps.AppsScreen
import ru.topbun.detail_mod.DetailModScreen
import ru.topbun.favorite.FavoriteScreen
import ru.topbun.instruction.InstructionScreen
import ru.topbun.main.MainScreen
import ru.topbun.navigation.SharedScreen
import ru.topbun.splash.SplashScreen
import ru.topbun.tabs.TabsScreen

fun initSharedScreens() {
    ScreenRegistry {
        register<SharedScreen.TabsScreen> {
            TabsScreen
        }
        register<SharedScreen.SplashScreen> {
            SplashScreen
        }
        register<SharedScreen.MainScreen> {
            MainScreen
        }
        register<SharedScreen.InstructionScreen> {
            InstructionScreen
        }
        register<SharedScreen.FeedbackScreen> {
            AppsScreen
        }
        register<SharedScreen.FavoriteScreen> {
            FavoriteScreen
        }
        register<SharedScreen.DetailModScreen> { provider ->
            DetailModScreen(provider.modId)
        }
    }
}