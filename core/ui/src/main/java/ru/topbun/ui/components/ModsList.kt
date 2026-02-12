package ru.topbun.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.topbun.domain.entity.mod.ModEntity

sealed class ModsListItem {
    data class ModItem(val mod: ModEntity) : ModsListItem()
    object AdItem : ModsListItem()
}

fun buildList(mods: List<ModEntity>, adNativeIntervalContent: Int): List<ModsListItem> {
    val result = ArrayList<ModsListItem>()
    mods.forEachIndexed { index, mod ->
        result += ModsListItem.ModItem(mod)

        if ((index + 1) % adNativeIntervalContent == 0) {
            result += ModsListItem.AdItem
        }
    }
    return result
}

@Composable
fun ModsList(
    mods: List<ModEntity>,
    state: LazyListState,
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isError: Boolean,
    isEndList: Boolean,
    adNativeIntervalContent: Int,
    adContent: @Composable () -> Unit,
    onClickFavorite: (ModEntity) -> Unit,
    onClickMod: (ModEntity) -> Unit,
    onLoad: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= mods.lastIndex - 3
        }
    }

    var loadingTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(shouldLoadMore.value, isLoading, isEndList) {
        if (shouldLoadMore.value && !isLoading && !isEndList && !loadingTriggered) {
            loadingTriggered = true
            onLoad()
        }

        if (!isLoading) {
            loadingTriggered = false
        }
    }

    LazyColumn(
        state = state,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        items(
            items = buildList(
                mods = mods,
                adNativeIntervalContent = adNativeIntervalContent
            ),
            key = { it.hashCode() },
        ) { item ->

            when (item) {
                is ModsListItem.ModItem -> {
                    ModItem(
                        mod = item.mod,
                        onClickFavorite = { onClickFavorite(item.mod) },
                        onClickMod = { onClickMod(item.mod) }
                    )
                }

                ModsListItem.AdItem -> {
                    adContent()
                }
            }
        }
        item {
            PaginationLoader(
                isEndList = isEndList,
                isLoading = isLoading,
                isError = isError,
                isEmpty = mods.isEmpty(),
                key = mods.size,
                onLoad = onLoad
            )
        }
    }
}
