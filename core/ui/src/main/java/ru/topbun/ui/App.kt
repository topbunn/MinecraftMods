package ru.topbun.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.Navigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.topbun.navigation.SharedScreen
import ru.topbun.ui.theme.Colors

@Composable
fun App() {
    val initScreen = rememberScreen(SharedScreen.SplashScreen)
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(Colors.BLACK_BG, false)
    Navigator(initScreen)
}
