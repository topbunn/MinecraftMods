package ru.topbun.favorite

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.favorite.FavoriteState.FavoriteScreenState.Error
import ru.topbun.favorite.FavoriteState.FavoriteScreenState.Loading
import ru.topbun.navigation.SharedScreen
import ru.topbun.ui.R
import ru.topbun.ui.components.AppButton
import ru.topbun.ui.components.ModItem
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
            val viewModel = viewModel<FavoriteViewModel>()
            val state by viewModel.state.collectAsState()

            LaunchedEffect(state.favoriteScreenState) {
                if (state.favoriteScreenState is Error) {
                    Toast.makeText(
                        context,
                        (state.favoriteScreenState as Error).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 20.dp)
            ) {
                item { Header(state) }
                when {
                    state.mods.isNotEmpty() -> {
                        state.mods.forEachIndexed { index, mod ->
                            item {
                                ModItem(
                                    mod = mod,
                                    onClickFavorite = { viewModel.removeFavorite(mod) },
                                    onClickMod = {
                                        viewModel.openMod(mod)
                                    }
                                )
                            }
                            if (index != 0 && ((index + 1) % 2 == 0)) {
                                item {
                                    NativeAdInitializer.show(context, Modifier.fillMaxWidth())
                                }
                            }
                            if (state.mods.size == 1) {
                                item {
                                    NativeAdInitializer.show(context, Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }

                    state.favoriteScreenState is Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Colors.WHITE,
                                    strokeWidth = 2.5.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    state.favoriteScreenState is Error -> {
                        item {
                            AppButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.retry)
                            ) { viewModel.loadMods() }
                        }
                    }

                    else -> {
                        item {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.the_list_is_empty),
                                style = Typography.APP_TEXT,
                                fontSize = 18.sp,
                                color = Colors.GRAY,
                                textAlign = TextAlign.Center,
                                fontFamily = Fonts.SF.BOLD,
                            )
                        }
                    }
                }
            }
            state.openMod?.let {
                val detailScreen = rememberScreen(SharedScreen.DetailModScreen(it.id))
                parentNavigator?.push(detailScreen)
                viewModel.openMod(null)
            }
            LaunchedEffect(this) {
                viewModel.loadMods()
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.favorite, state.mods.count()),
            style = Typography.APP_TEXT,
            fontSize = 22.sp,
            color = Colors.GRAY,
            fontFamily = Fonts.SF.BOLD,
        )
    }
}