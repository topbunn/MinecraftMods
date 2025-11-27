package ru.topbun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.ui.theme.Colors

@Composable
fun DialogWrapper(
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
        .heightIn(max = 700.dp)
        .padding(horizontal = 20.dp)
        .background(color = Colors.GRAY_BG, RoundedCornerShape(8.dp))
        .padding(horizontal = 16.dp, vertical = 20.dp)
        .clipToBounds(),
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissDialog,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        val context = LocalContext.current
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                content = content
            )
            Box(Modifier.padding(horizontal = 20.dp)) {
                NativeAdInitializer.show(context, Modifier.fillMaxSize())
            }
        }
    }
}