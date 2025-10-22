package ru.topbun.detail_mod

import android.Manifest
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import kotlinx.parcelize.Parcelize
import ru.topbun.android.utills.getModNameFromUrl
import ru.topbun.detail_mod.dontWorkAddon.DontWorkAddonDialog
import ru.topbun.detail_mod.setupMod.SetupModDialog
import ru.topbun.domain.entity.mod.ModEntity
import ru.topbun.navigation.SharedScreen
import ru.topbun.ui.R
import ru.topbun.ui.components.AppButton
import ru.topbun.ui.components.IconWithButton
import ru.topbun.ui.components.InterstitialAd
import ru.topbun.ui.components.NativeAd
import ru.topbun.ui.components.noRippleClickable
import ru.topbun.ui.components.rippleClickable
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography
import ru.topbun.ui.utils.requestPermissions

@Parcelize
data class DetailModScreen(private val modId: Int) : Screen, Parcelable {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val activity = LocalActivity.currentOrThrow

        val viewModel = remember { DetailModViewModel(activity.applicationContext, modId) }
        val state by viewModel.state.collectAsState()
        val config = state.config
        val loadModState = state.loadModState

        var interAdIsShown by rememberSaveable{
            mutableStateOf(false)
        }

        if (!interAdIsShown && config != null){
            InterstitialAd(activity, config.isAdEnabled, config.yandexInter, config.applovinInter){
                interAdIsShown = true
            }
        }

        requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

        LaunchedEffect(loadModState) {
            if (loadModState is DetailModState.LoadModState.Error){
                Toast.makeText(activity.application, "Loading error. Check internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.GRAY_BG)
                .navigationBarsPadding()
                .statusBarsPadding()
                .background(Colors.BLACK_BG)
        ) {
            Header(viewModel, state)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                state.mod?.let {
                    ButtonInstruction(navigator)
                    Spacer(Modifier.height(10.dp))
                    Preview(it)
                    Spacer(Modifier.height(20.dp))
                    TitleWithDescr(viewModel, state)
//                    Spacer(Modifier.height(10.dp))
//                    Metrics(it)
                    Spacer(Modifier.height(20.dp))
                    SupportVersions(state)
                    config?.let {
                        Spacer(Modifier.height(20.dp))
                        NativeAd(activity.application, it.isAdEnabled, it.yandexNative, it.applovinNative)
                    }
                    Spacer(Modifier.height(20.dp))
                    FileButtons(viewModel, state)
                }
                Box(Modifier.fillMaxWidth(), Alignment.Center){
                    when(loadModState) {
                        is DetailModState.LoadModState.Error ->  {
                            AppButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.retry)
                            ) { viewModel.loadMod() }
                        }
                        DetailModState.LoadModState.Loading -> {
                            CircularProgressIndicator(
                                color = Colors.WHITE,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
        state.mod?.let { mod ->
            state.choiceFilePathSetup?.let {
                SetupModDialog(
                    it.getModNameFromUrl(mod.category.toExtension()),
                    config,
                    viewModel
                ) {
                    viewModel.changeStageSetupMod(null)
                }
            }
            if (state.dontWorkAddonDialogIsOpen){
                DontWorkAddonDialog(config) { viewModel.openDontWorkDialog(false) }
            }
        }
    }

}


@Composable
private fun FileButtons(viewModel: DetailModViewModel, state: DetailModState) {
    state.mod?.let { mod ->
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            mod.files.forEach {
                AppButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    text = it.getModNameFromUrl(mod.category.toExtension()),
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                ) {
                    viewModel.changeStageSetupMod(it)
                }
            }
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                text = stringResource(R.string.addon_don_t_work),
                contentColor = Colors.WHITE,
                containerColor = Color(0xffE03131),
            ) {
                viewModel.openDontWorkDialog(true)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SupportVersions(state: DetailModState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.supported_versions),
            style = Typography.APP_TEXT,
            fontSize = 18.sp,
            color = Colors.WHITE,
            fontFamily = Fonts.SF.SEMI_BOLD,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            state.mod?.versions?.forEach { version ->
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
        fontFamily = Fonts.SF.SEMI_BOLD,
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
private fun TitleWithDescr(viewModel: DetailModViewModel, state: DetailModState) {
    state.mod?.let { mod ->
        Text(
            text = mod.title,
            style = Typography.APP_TEXT,
            fontSize = 24.sp,
            color = Colors.WHITE,
            fontFamily = Fonts.SF.BOLD,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (state.descriptionTextExpand) mod.description else mod.description.take(300) + "...",
            style = Typography.APP_TEXT,
            fontSize = 14.sp,
            color = Colors.GRAY,
            fontFamily = Fonts.SF.MEDIUM,
        )
        Spacer(Modifier.height(10.dp))
        if (mod.description.length > 300){
            Box(Modifier.fillMaxWidth(), Alignment.CenterEnd){
                Row(
                    modifier = Modifier
                        .rippleClickable() { viewModel.switchDescriptionExpand() }
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    Text(
                        text = stringResource(if(state.descriptionTextExpand) R.string.collapse else R.string.expand),
                        style = Typography.APP_TEXT,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = Fonts.SF.BOLD,
                    )
                    Icon(
                        modifier = Modifier.rotate(if (state.descriptionTextExpand) 180f else 0f),
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
        ){
            val countTake = if (state.descriptionImageExpand) Int.MAX_VALUE else 3
            mod.descriptionImages.take(countTake).forEach {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    model = it,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        if (mod.descriptionImages.count() > 5){
            Box(Modifier.fillMaxWidth(), Alignment.CenterEnd){
                Row(
                    modifier = Modifier
                        .rippleClickable() { viewModel.switchDescriptionImageExpand() }
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    Text(
                        text = stringResource(if(state.descriptionImageExpand) R.string.collapse else R.string.expand),
                        style = Typography.APP_TEXT,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = Fonts.SF.BOLD,
                    )
                    Icon(
                        modifier = Modifier.rotate(if (state.descriptionImageExpand) 180f else 0f),
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
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        model = mod.image,
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
            .height(48.dp)
    ) {
        navigator.push(instructionScreen)
    }
}

@Composable
private fun Header(viewModel: DetailModViewModel, state: DetailModState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colors.GRAY_BG)
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
            fontFamily = Fonts.SF.BOLD,
        )

        Image(
            modifier = Modifier
                .size(24.dp)
                .noRippleClickable { viewModel.changeFavorite() },
            painter = painterResource(
                if (state.mod?.isFavorite ?: false) R.drawable.ic_mine_heart_filled else R.drawable.ic_mine_heart_stroke
            ),
            contentDescription = "favorite mods",
        )
    }
}