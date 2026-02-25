package ru.topbun.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.Navigator
import ru.topbun.navigation.SharedScreen

@Composable
fun App() {
    val initScreen = rememberScreen(SharedScreen.SplashScreen)
    Navigator(initScreen)
}
