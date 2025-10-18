package ru.topbun.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun getImageWithNameFile(name: String): Int {
    val context = LocalContext.current
    return remember(name) {
        context.resources.getIdentifier(
            name,
            "drawable",
            context.packageName
        )
    }
}