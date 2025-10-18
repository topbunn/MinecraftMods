package ru.topbun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography.APP_TEXT

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    alignment: Alignment.Vertical = Alignment.CenterVertically,
    iconStart: (@Composable () -> Unit)? = null,
    padding: PaddingValues = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    background: Color = Colors.GRAY_BG,
    textAlignment: Alignment = Alignment.CenterStart,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Row(
        modifier = modifier.background(background, RoundedCornerShape(6.dp)),
        verticalAlignment = alignment
    ){
        iconStart?.let {
            Spacer(modifier = Modifier.width(12.dp))
            it()
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(padding),
            contentAlignment = textAlignment
        ){
            if (value.isEmpty()){
                Text(
                    text = placeholder,
                    style = APP_TEXT,
                    fontSize = 16.sp,
                    fontFamily = Fonts.SF.MEDIUM,
                    color = Colors.GRAY.copy(0.7f)
                )
            }
            val textStyle = TextStyle(
                color = Colors.GRAY,
                fontSize = 16.sp,
                fontFamily = Fonts.SF.MEDIUM
            )
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                textStyle = textStyle,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                minLines = minLines,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                visualTransformation = visualTransformation
            )

        }
    }
}