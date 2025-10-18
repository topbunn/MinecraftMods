package ru.topbun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0xff464646),
    color: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(2.dp),
) {
    Box(
        modifier = modifier
            .background(trackColor)
            .clip(shape)
    ){
        Box(
            modifier = Modifier.fillMaxHeight()
                .fillMaxWidth(progress)
                .background(color)
                .clip(shape)
        )
    }
}