package ru.topbun.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.domain.entity.mod.ModEntity

@Composable
fun ModsList(
    mods: List<ModEntity>,
    state: LazyListState,
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isError: Boolean,
    isEndList: Boolean,
    onClickFavorite: (ModEntity) -> Unit,
    onClickMod: (ModEntity) -> Unit,
    onLoad: () -> Unit,
) {
    LazyColumn(
        state = state,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        itemsIndexed(mods, key = { _, mod -> mod.id }) { index, mod ->
            ModItem(
                mod = mod,
                onClickFavorite = { onClickFavorite(mod) },
                onClickMod = { onClickMod(mod) }
            )
            if (index != 0 && ((index + 1) % 3 == 0)) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    NativeAdInitializer.show(Modifier.fillMaxWidth())
                }
            }
        }
        item{
            PaginationLoader(
                isEndList = isEndList,
                isLoading = isLoading,
                isError = isError,
                isEmpty = mods.isEmpty(),
                key = mods,
                onClickRetryLoad = onLoad,
                onLoad = onLoad,
            )
        }
    }
}