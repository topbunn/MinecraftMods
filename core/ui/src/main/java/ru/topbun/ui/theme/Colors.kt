package ru.topbun.ui.theme

import androidx.compose.ui.graphics.Color
import org.koin.mp.KoinPlatform.getKoin
import ru.topbun.domain.entity.modConfig.ModConfigProvider
import ru.topbun.ui.BuildConfig

object Colors {

    private val configProvider: ModConfigProvider
        get() = getKoin().get()

    val WHITE = Color(0xFFFFFFFF)
    val GRAY = Color(0xFFD4D3D3)
    val BUTTON_RED = Color(0xFFFF1A1A)

    val BLACK_BG = Color(0xFF111111)
    val GRAY_BG = Color(0xFF1B1B1B)

    val PRIMARY = Color(configProvider.getConfig().primaryColor)
}