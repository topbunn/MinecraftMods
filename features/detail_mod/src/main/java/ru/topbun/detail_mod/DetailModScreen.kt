package ru.topbun.detail_mod

import android.Manifest
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf
import ru.topbun.android.ads.inter.InterAdInitializer
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.android.utills.getModNameFromUrl
import ru.topbun.detail_mod.dontWorkAddon.DontWorkAddonDialog
import ru.topbun.detail_mod.setupMod.SetupModDialog
import ru.topbun.domain.entity.mod.ModEntity
import ru.topbun.navigation.SharedScreen
import ru.topbun.ui.R
import ru.topbun.ui.components.AppButton
import ru.topbun.ui.components.IconWithButton
import ru.topbun.ui.components.noRippleClickable
import ru.topbun.ui.components.rippleClickable
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography
import ru.topbun.ui.utils.requestPermissions

@Parcelize
data class DetailModScreen(private val modId: Int) : Screen, Parcelable {

    override val key = modId.toString()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val activity = LocalActivity.currentOrThrow

        val viewModel = koinScreenModel<DetailModViewModel> { parametersOf(modId) }
        val state by viewModel.state.collectAsState()
        val loadModState = state.loadModState

        requestPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        LaunchedEffect(loadModState) {
            if (loadModState is DetailModState.LoadModState.Error) {
                Toast.makeText(
                    activity.application,
                    "Loading error. Check internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        Box{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Colors.BLACK_BG)
                    .navigationBarsPadding()
                    .statusBarsPadding()
            ) {
                Header(
                    mod = state.mod,
                    onClickChangeFavorite = {viewModel.changeFavorite()}
                )
                PullToRefreshBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    isRefreshing = false,
                    onRefresh = { viewModel.loadMod() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        state.mod?.let { mod ->
                            Preview(mod)
                            DetailCard {
                                TitleWithDescr(
                                    mod = mod,
                                    descriptionTextExpand = state.descriptionTextExpand,
                                    descriptionImageExpand = state.descriptionImageExpand,
                                    onClickSwitchDescriptionImage = viewModel::switchDescriptionImageExpand,
                                    onClickSwitchDescriptionText = viewModel::switchDescriptionTextExpand
                                )
                            }

                            DetailCard {
                                SupportVersions(mod = mod)
                            }

                            NativeAdInitializer.show(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .border(2.dp, Colors.PRIMARY, RoundedCornerShape(22.dp))
                            )

                            DetailCard {
                                Text(
                                    text = stringResource(R.string.files_for_download),
                                    style = Typography.APP_TEXT,
                                    fontSize = 18.sp,
                                    color = Colors.WHITE,
                                    fontFamily = Fonts.INTER.SEMI_BOLD,
                                )
                                ButtonInstruction(navigator)
                                FileButtons(
                                    mod = mod,
                                    onClickMod = { viewModel.changeStageSetupMod(it) },
                                    onClickAddonNotWork = { viewModel.openDontWorkDialog(true) },
                                )
                            }
                            Spacer(Modifier.height(32.dp))
                        }
                    }

                    Box(Modifier.fillMaxWidth(), Alignment.Center) {
                        when (loadModState) {
                            is DetailModState.LoadModState.Error -> {
                                AppButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(R.string.retry)
                                ) { viewModel.loadMod() }
                            }

                            DetailModState.LoadModState.Loading -> {
                                Box(Modifier.padding(vertical = 20.dp)){
                                    CircularProgressIndicator(
                                        color = Colors.WHITE,
                                        strokeWidth = 2.5.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
            CountDownTimerWithInterAd(Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp))


        }
        state.mod?.let { mod ->
            state.choiceFilePathSetup?.let {
                SetupModDialog(
                    it.getModNameFromUrl(mod.category.toExtension()),
                    viewModel
                ) {
                    viewModel.changeStageSetupMod(null)
                }
            }
            if (state.dontWorkAddonDialogIsOpen) {
                DontWorkAddonDialog() { viewModel.openDontWorkDialog(false) }
            }
        }
    }

}

@Composable
fun DetailCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Colors.GRAY_BG)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun CountDownTimerWithInterAd(modifier: Modifier) {
    val activity = LocalActivity.currentOrThrow

    var timeLeft by remember { mutableStateOf(3) }
    var adShowed by rememberSaveable { mutableStateOf(false) }
    var adReady by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        adReady = InterAdInitializer.isReadyToShow()
        if (!adShowed && adReady){
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }

            adShowed = true
            InterAdInitializer.show(activity)
        }
    }
    if (!adShowed && adReady){
        Text(
            modifier = modifier
                .background(color = Color.White, CircleShape)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            text = stringResource(R.string.ads_through, timeLeft),
            style = Typography.APP_TEXT,
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = Fonts.INTER.MEDIUM,
        )
    }

}


@Composable
private fun FileButtons(
    mod: ModEntity?,
    onClickMod: (path: String) -> Unit,
    onClickAddonNotWork: () -> Unit,
) {
    mod?.let { mod ->
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            mod.files.forEach {
                AppButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp),
                    text = it.getModNameFromUrl(mod.category.toExtension()),
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                ) {
                    onClickMod(it)
                }
            }
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 40.dp),
                text = stringResource(R.string.addon_don_t_work),
                contentColor = Colors.WHITE,
                containerColor = Color(0xffE03131),
            ) {
                onClickAddonNotWork()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SupportVersions(
    mod: ModEntity?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.supported_versions),
            style = Typography.APP_TEXT,
            fontSize = 18.sp,
            color = Colors.WHITE,
            fontFamily = Fonts.INTER.SEMI_BOLD,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            mod?.versions?.forEach { version ->
                SupportVersionItem(
                    value = version,
                )
            }
        }
    }
}

@Composable
private fun SupportVersionItem(value: String, actualVersion: Boolean = false) {
    Text(
        modifier = Modifier
            .background(
                if (actualVersion) MaterialTheme.colorScheme.primary else Colors.WHITE,
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        text = value,
        style = Typography.APP_TEXT,
        fontSize = 15.sp,
        color = Colors.BLACK_BG,
        fontFamily = Fonts.INTER.SEMI_BOLD,
    )
}

@Composable
private fun Metrics(mod: ModEntity) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        IconWithButton(mod.rating.toString(), R.drawable.ic_star)
        IconWithButton(mod.commentCounts.toString(), R.drawable.ic_comment)
    }
}

@Composable
private fun TitleWithDescr(
    mod: ModEntity?,
    descriptionTextExpand: Boolean,
    descriptionImageExpand: Boolean,
    onClickSwitchDescriptionImage: () -> Unit,
    onClickSwitchDescriptionText: () -> Unit,
) {
    mod?.let { mod ->
        Text(
            text = mod.title,
            style = Typography.APP_TEXT,
            fontSize = 24.sp,
            color = Colors.WHITE,
            fontFamily = Fonts.INTER.BOLD,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (descriptionTextExpand) mod.description else mod.description.take(300) + "...",
            style = Typography.APP_TEXT,
            fontSize = 14.sp,
            color = Colors.GRAY,
            fontFamily = Fonts.INTER.MEDIUM,
        )
        Spacer(Modifier.height(10.dp))
        if (mod.description.length > 300) {
            Box(Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                Row(
                    modifier = Modifier
                        .rippleClickable() { onClickSwitchDescriptionText() }
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(if (descriptionTextExpand) R.string.collapse else R.string.expand),
                        style = Typography.APP_TEXT,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = Fonts.INTER.BOLD,
                    )
                    Icon(
                        modifier = Modifier.rotate(if (descriptionTextExpand) 180f else 0f),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Choice type",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val context = LocalContext.current
            val countTake = if (descriptionImageExpand) Int.MAX_VALUE else 3
            mod.descriptionImages.take(countTake).forEach {
                val request = remember(it) {
                    ImageRequest.Builder(context)
                        .data(it)
                        .crossfade(false)
                        .memoryCacheKey(it)
                        .diskCacheKey(it)
                        .build()
                }
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    model = request,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        if (mod.descriptionImages.count() > 5) {
            Box(Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                Row(
                    modifier = Modifier
                        .rippleClickable() { onClickSwitchDescriptionImage() }
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(if (descriptionImageExpand) R.string.collapse else R.string.expand),
                        style = Typography.APP_TEXT,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = Fonts.INTER.BOLD,
                    )
                    Icon(
                        modifier = Modifier.rotate(if (descriptionImageExpand) 180f else 0f),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Choice type",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun Preview(mod: ModEntity) {
    val context = LocalContext.current
    val request = remember(mod.image) {
        ImageRequest.Builder(context)
            .data(mod.image)
            .crossfade(false)
            .memoryCacheKey(mod.image)
            .diskCacheKey(mod.image)
            .build()
    }
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        model = request,
        contentDescription = mod.title,
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun ButtonInstruction(navigator: Navigator) {
    val instructionScreen = rememberScreen(SharedScreen.InstructionScreen)
    AppButton(
        text = stringResource(R.string.instructions),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
    ) {
        navigator.push(instructionScreen)
    }
}

@Composable
private fun Header(
    mod: ModEntity?,
    onClickChangeFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.BLACK_BG)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navigator = LocalNavigator.currentOrThrow
        Icon(
            modifier = Modifier
                .height(20.dp)
                .noRippleClickable { navigator.pop() },
            painter = painterResource(R.drawable.ic_back),
            contentDescription = "button back",
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.installation),
            style = Typography.APP_TEXT,
            fontSize = 18.sp,
            color = Colors.GRAY,
            fontFamily = Fonts.INTER.BOLD,
        )

        mod?.let {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .rippleClickable{ onClickChangeFavorite() },
                painter = painterResource(R.drawable.ic_favorite),
                contentDescription = "status favorite mod",
                tint = if (mod.isFavorite) Color.Red else Color.White.copy(0.2f)
            )
        } ?: run {
            Box{}
        }

    }
}