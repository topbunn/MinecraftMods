package ru.topbun.main

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.main.MainState.MainScreenState.Error
import ru.topbun.main.MainState.MainScreenState.Loading
import ru.topbun.main.di.MainEvents
import ru.topbun.navigation.SharedScreen
import ru.topbun.ui.R
import ru.topbun.ui.components.CustomInputField
import ru.topbun.ui.components.ModsList
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.utils.ObserveAsEvents

object MainScreen : Tab, Screen {

    override val options: TabOptions
        @Composable get() = TabOptions(
            0U,
            stringResource(R.string.tabs_main),
            painterResource(R.drawable.ic_tabs_main)
        )

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.BLACK_BG)
                .padding(top = 24.dp, start = 12.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val parentNavigator = LocalNavigator.currentOrThrow.parent

            val context = LocalContext.current
            val viewModel = koinScreenModel<MainViewModel>()
            val state by viewModel.state.collectAsState()

            val isCollapsed by remember { derivedStateOf { state.modListState.firstVisibleItemIndex > 0 } }

            LaunchedEffect(Unit) {
                viewModel.handleChangeState()
            }

            ObserveAsEvents(viewModel.events) {
                when(it){
                    is MainEvents.ShowError -> {
                        val message = context.getString(R.string.error_check_internet)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Colors.GRAY_BG)
                    .animateContentSize(tween(400))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TopBar(
                    value = state.search,
                    onValueChange = { viewModel.changeSearch(it) },
                )
                if (!isCollapsed) {
                    OptionBar(
                        modifier = Modifier.fillMaxWidth(),
                        modTypeUis = state.modTypeUis,
                        modSorts = state.modSorts,
                        modSortSelectedIndex = state.modSortSelectedIndex,
                        selectedModTypeUi = state.selectedModTypeUi,
                        changeModSort = { viewModel.changeModSort(it) },
                        changeSortType = { viewModel.changeSortType(it) },
                    )
                }
            }
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                isRefreshing = false,
                onRefresh = { viewModel.refreshMods() }
            ) {
                ModsList(
                    modifier = Modifier.fillMaxSize(),
                    mods = state.mods,
                    state = state.modListState,
                    isError = state.mainScreenState is Error,
                    isLoading = state.mainScreenState is Loading,
                    isEndList = state.isEndList,
                    adNativeIntervalContent = state.config?.adNativeIntervalContent ?: 3,
                    adContent = {
                        NativeAdInitializer.show(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .border(2.dp, Colors.PRIMARY, RoundedCornerShape(22.dp))
                        )
                    },
                    onLoad = { viewModel.loadMods() },
                    onClickFavorite = { viewModel.changeFavorite(it) },
                    onClickMod = { viewModel.changeOpenMod(it) }
                )
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
private fun TopBar(
    value: String,
    onValueChange: (String) -> Unit,
) {
    CustomInputField(
        modifier = Modifier.fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp),
        value = value,
        placeholder = stringResource(R.string.search),
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "search",
                tint = Colors.GRAY
            )
        }
    )
}
