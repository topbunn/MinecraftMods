package ru.topbun.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 15.sp,
                fontFamily = Fonts.INTER.REGULAR,
                color = Colors.WHITE.copy(0.3f)
            )
        },
        singleLine = singleLine,
        textStyle = TextStyle(
            fontSize = 15.sp,
            fontFamily = Fonts.INTER.REGULAR,
            color = Colors.WHITE.copy(0.7f)
        ),
        leadingIcon = leadingIcon,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Colors.PRIMARY,
            unfocusedBorderColor = Color.White.copy(0.1f),
            cursorColor = Colors.PRIMARY,
            unfocusedContainerColor = Colors.GRAY_BG,
            focusedContainerColor = Colors.GRAY_BG,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}