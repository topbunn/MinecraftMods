package ru.topbun.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ru.topbun.navigation.SharedScreen
import ru.topbun.ui.components.BottomNavigationItem
import ru.topbun.ui.theme.Colors

object TabsScreen: Screen {

    @Composable
    override fun Content() {
        val mainScreen = rememberScreen(SharedScreen.MainScreen) as Tab
        TabNavigator(tab = mainScreen){
            Scaffold(
                modifier = Modifier.Companion.background(Colors.GRAY_BG).navigationBarsPadding(),
                content = {
                    Box(Modifier.Companion.fillMaxSize().padding(it)) {
                        CurrentTab()
                    }
                },
                bottomBar = {
                    Column {
                        Spacer(
                            Modifier.Companion.fillMaxWidth().height(1.dp)
                                .background(Colors.WHITE.copy(0.15f))
                        )
                        Row(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .background(Colors.GRAY_BG)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Companion.CenterVertically
                        ) {
                            val favoriteScreen = rememberScreen(SharedScreen.FavoriteScreen) as Tab
                            val feedbackScreen = rememberScreen(SharedScreen.FeedbackScreen) as Tab
                            BottomNavigationItem(mainScreen)
                            BottomNavigationItem(favoriteScreen)
                            BottomNavigationItem(feedbackScreen)
                        }
                    }
                }
            )
        }
    }

}