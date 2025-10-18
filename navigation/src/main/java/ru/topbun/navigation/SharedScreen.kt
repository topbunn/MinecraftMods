package ru.topbun.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SharedScreen : ScreenProvider {
    object TabsScreen : SharedScreen()
    object SplashScreen : SharedScreen()
    object MainScreen : SharedScreen()
    object InstructionScreen : SharedScreen()
    object FeedbackScreen : SharedScreen()
    object FavoriteScreen : SharedScreen()
    data class DetailModScreen(val modId: Int) : SharedScreen()
}