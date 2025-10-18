package ru.topbun.apps

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ru.topbun.ui.R
import ru.topbun.ui.components.AppButton
import ru.topbun.ui.components.AppInfoItem
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography

object AppsScreen : Tab, Screen {

    override val options: TabOptions
        @Composable get() = TabOptions(
            2U,
            stringResource(R.string.tabs_apps),
            painterResource(R.drawable.ic_tabs_apps)
        )

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.BLACK_BG)
                .padding(top = 20.dp)
        ) {
            val context = LocalContext.current
            val viewModel = viewModel<AppsViewModel>()
            val state by viewModel.state.collectAsState()
            LaunchedEffect(state.screenState) {
                if (state.screenState is Error) {
                    Toast.makeText(
                        context,
                        (state.screenState as Error).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            Header()
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 20.dp)
            ) {
                when {
                    state.appsInfo.isNotEmpty() -> {
                        state.appsInfo.forEach {
                            item {
                                AppInfoItem(it.logoLink, it.name) {
                                    viewModel.openApp(it)
                                }
                            }
                        }
                    }

                    state.screenState is AppsState.AppsStateScreen.Loading -> {
                        item(span = { GridItemSpan(3) }) {
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

                    state.screenState is AppsState.AppsStateScreen.Error -> {
                        item(span = { GridItemSpan(3) }) {
                            AppButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.retry)
                            ) { viewModel.loadApps() }
                        }
                    }

                    else -> {
                        item(span = { GridItemSpan(3) }) {
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
        }
    }

    @Composable
    private fun Header() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.you_might_like),
                style = Typography.APP_TEXT,
                fontSize = 22.sp,
                color = Colors.GRAY,
                fontFamily = Fonts.SF.BOLD,
            )
        }
    }

}