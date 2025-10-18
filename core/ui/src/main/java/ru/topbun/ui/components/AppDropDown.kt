package ru.topbun.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography

@Composable
fun AppDropDown(
    value: String,
    items: List<String>,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {

    var mExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = modifier.clickable { mExpanded = !mExpanded },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                style = Typography.APP_TEXT,
                fontSize = 15.sp,
                color = Colors.GRAY,
                fontFamily = Fonts.SF.MEDIUM
            )
            Icon(
                modifier = Modifier.rotate(if (mExpanded) 180f else 0f),
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Choice type",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            containerColor = Colors.GRAY_BG,
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(100.dp)
                .heightIn(max = 500.dp)
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it,
                            style = Typography.APP_TEXT,
                            fontSize = 14.sp,
                            color = Colors.GRAY,
                            fontFamily = Fonts.SF.MEDIUM
                        )
                    },
                    onClick = {
                        onValueChange(it)
                        mExpanded = false
                    }
                )
            }
        }
    }
}
