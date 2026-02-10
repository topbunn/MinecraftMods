package ru.topbun.navigation

import android.graphics.pdf.content.PdfPageGotoLinkContent
import android.os.Parcelable
import cafe.adriel.voyager.core.registry.ScreenProvider
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SharedScreen : ScreenProvider, Parcelable {
    object TabsScreen : SharedScreen()
    object SplashScreen : SharedScreen()
    object MainScreen : SharedScreen()
    object InstructionScreen : SharedScreen()
    object FeedbackScreen : SharedScreen()
    object FavoriteScreen : SharedScreen()
    data class FullscreenAdScreen(val screen: SharedScreen) : SharedScreen()
    data class DetailModScreen(val modId: Int) : SharedScreen()
}