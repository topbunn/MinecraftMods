package ru.topbun.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts

@Composable
fun SendButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.PRIMARY,
            disabledContainerColor = Colors.PRIMARY.copy(0.5f),
            contentColor = Colors.WHITE,
            disabledContentColor = Colors.WHITE.copy(0.5f),
        )
    ) {
        if (isLoading){
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Colors.WHITE, strokeWidth = 1.5.dp)
        } else {
            Text(
                text = stringResource(ru.topbun.ui.R.string.send),
                fontSize = 16.sp,
                fontFamily = Fonts.INTER.MEDIUM,
                fontWeight = FontWeight.Medium
            )
        }
    }
}