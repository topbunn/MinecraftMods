package ru.topbun.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography

@Composable
fun RowScope.BottomNavigationItem(
    tab: Tab,
) {
    val tabNavigator = LocalTabNavigator.current
    val selected = tabNavigator.current == tab
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(indication = null, interactionSource = interaction) {
                tabNavigator.current = tab
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color = if(selected) MaterialTheme.colorScheme.primary else Colors.WHITE.copy(0.5f)
        tab.options.icon?.let {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = it,
                contentDescription = tab.options.title,
                tint = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = tab.options.title,
            style = Typography.APP_TEXT,
            fontSize = 14.sp,
            color = color,
            fontFamily = Fonts.SF.SEMI_BOLD,
        )

    }
}
