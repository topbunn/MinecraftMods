package ru.topbun.favorite

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.favorite.FavoriteState.FavoriteScreenState.Error
import ru.topbun.favorite.FavoriteState.FavoriteScreenState.Loading
import ru.topbun.navigation.SharedScreen
import ru.topbun.ui.R
import ru.topbun.ui.components.ModItem
import ru.topbun.ui.components.PaginationLoader
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography

object FavoriteScreen : Tab, Screen {

    override val options: TabOptions
        @Composable get() = TabOptions(
            0U,
            stringResource(R.string.tabs_favorite),
            painterResource(R.drawable.ic_tabs_favorite)
        )


    @Composable
    override fun Content() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.BLACK_BG)
        ) {
            val context = LocalContext.current
            val parentNavigator = LocalNavigator.currentOrThrow.parent
            val viewModel = koinScreenModel<FavoriteViewModel>()
            val state by viewModel.state.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.resetMods()
            }

            LaunchedEffect(state.favoriteScreenState) {
                if (state.favoriteScreenState is Error) {
                    Toast.makeText(
                        context,
                        (state.favoriteScreenState as Error).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            PullToRefreshBox(
                isRefreshing = false,
                onRefresh = { viewModel.resetMods() }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 10.dp, horizontal = 12.dp)
                ) {
                    item { Header(state) }
                    itemsIndexed(items = state.mods, key = { _, mod -> mod.id }) { index, mod ->
                        ModItem(
                            mod = mod,
                            onClickFavorite = { viewModel.changeFavorite(mod) },
                            onClickMod = {
                                viewModel.changeOpenMod(mod)
                            }
                        )
                        if ((index != 0 && ((index + 1) % 3 == 0)) || state.mods.size == 1) {
                            Column {
                                Spacer(Modifier.height(10.dp))
                                NativeAdInitializer.show(
                                    Modifier.fillMaxWidth()
                                        .heightIn(min = 300.dp)
                                        .clip(RoundedCornerShape(22.dp))
                                        .border(2.dp, Colors.PRIMARY, RoundedCornerShape(22.dp))
                                )
                            }
                        }
                    }
                    item {
                        PaginationLoader(
                            isEndList = state.isEndList,
                            isLoading = state.favoriteScreenState is Loading,
                            isError = state.favoriteScreenState is Error,
                            isEmpty = state.mods.isEmpty(),
                            key = state.mods.size,
                            onLoad = { viewModel.loadMods() },
                        )
                    }
                }
            }
            state.openMod?.let {
                val detailScreen = rememberScreen(SharedScreen.DetailModScreen(it.id))
                parentNavigator?.push(detailScreen)
                viewModel.changeOpenMod(null)
            }
        }
    }

}

@Composable
private fun Header(state: FavoriteState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 14.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.favorite))
                append(" (${state.favoriteSize ?: "0"})")
            },
            style = Typography.APP_TEXT,
            fontSize = 22.sp,
            color = Colors.WHITE,
            fontFamily = Fonts.INTER.BOLD,
        )
    }
}