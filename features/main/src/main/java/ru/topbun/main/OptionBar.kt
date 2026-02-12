package ru.topbun.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.topbun.ui.R
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts

@Composable
fun OptionBar(
    modifier: Modifier = Modifier,
    modTypeUis: List<ModTypeUi>,
    modSorts: List<ModSortTypeUi>,
    modSortSelectedIndex: Int,
    selectedModTypeUi: ModTypeUi,
    changeModSort: (Int) -> Unit,
    changeSortType: (ModTypeUi) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SortDropDown(
            modifier = Modifier.weight(1f),
            items = modSorts.map { stringResource(it.stringRes) },
            selectedIndex = modSortSelectedIndex,
            onValueChange = { changeModSort(it) }
        )
        CategoryDropDown(
            items = modTypeUis.map { it to stringResource(it.titleRes) },
            value = stringResource(selectedModTypeUi.titleRes),
            onValueChange = { changeSortType(it) }
        )
    }
}

@Composable
fun SortDropDown(
    items: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit,
) {

    var mExpanded by remember { mutableStateOf(false) }

    Column(modifier) {
        Row(
            modifier = Modifier.height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, Colors.WHITE.copy(0.1f), RoundedCornerShape(14.dp))
                .clickable { mExpanded = !mExpanded }
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = items[selectedIndex],
                fontFamily = Fonts.INTER.SEMI_BOLD,
                fontSize = 14.sp,
                color = Colors.WHITE
            )
            Icon(
                modifier = Modifier.size(24.dp).rotate(if (mExpanded) 180f else 0f),
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = "Arrow down",
                tint = Color.White
            )
        }

        DropdownMenu(
            shape = RoundedCornerShape(14.dp),
            containerColor = Colors.BLACK_BG,
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            fontFamily = Fonts.INTER.SEMI_BOLD,
                            fontSize = 14.sp,
                            color = Colors.WHITE
                        )
                    },
                    onClick = {
                        onValueChange(index)
                        mExpanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun CategoryDropDown(
    items: List<Pair<ModTypeUi, String>>,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (ModTypeUi) -> Unit,
) {

    var mExpanded by remember { mutableStateOf(false) }

    Column(modifier) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Colors.PRIMARY)
                .clickable { mExpanded = !mExpanded }
                .padding(12.dp),
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_category),
                contentDescription = "Category icon",
                tint = Color.White
            )
        }

        DropdownMenu(
            containerColor = Colors.BLACK_BG,
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.second,
                            fontFamily = Fonts.INTER.SEMI_BOLD,
                            fontSize = 14.sp,
                            color = Colors.WHITE
                        )
                    },
                    onClick = {
                        onValueChange(it.first)
                        mExpanded = false
                    }
                )
            }
        }
    }
}
