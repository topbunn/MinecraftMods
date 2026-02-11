package ru.topbun.detail_mod.setupMod

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.android.utills.getModFile
import ru.topbun.detail_mod.DetailModState.DownloadModState.Error
import ru.topbun.detail_mod.DetailModState.DownloadModState.Loading
import ru.topbun.detail_mod.DetailModState.DownloadModState.Success
import ru.topbun.detail_mod.DetailModViewModel
import ru.topbun.ui.R
import ru.topbun.ui.components.AppButton
import ru.topbun.ui.components.DialogWrapper
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography
import java.io.File

@Composable
fun SetupModDialog(
    fileName: String,
    viewModel: DetailModViewModel,
    onDismissDialog: () -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.currentOrThrow
    val state by viewModel.state.collectAsState()
    var savedFile by rememberSaveable { mutableStateOf<File?>(null) }
    LaunchedEffect(fileName) { savedFile = getModFile(fileName) }
    LaunchedEffect(state.downloadState) {
        when(val downloadState = state.downloadState){
            is Error -> Toast.makeText(context, downloadState.message, Toast.LENGTH_SHORT).show()
            Success -> savedFile = getModFile(fileName)
            else -> {}
        }
    }
    DialogWrapper(
        onDismissDialog = onDismissDialog,
        adContent = { NativeAdInitializer.show(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .border(2.dp, Colors.PRIMARY, RoundedCornerShape(22.dp))
                            ) }
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(
                if (savedFile != null) R.string.to_install_in_game_click_the_install else R.string.to_save_click_the_download_button
            ),
            style = Typography.APP_TEXT,
            fontSize = 16.sp,
            color = Colors.GRAY,
            fontFamily = Fonts.INTER.MEDIUM,
        )
        Spacer(Modifier.height(16.dp))

        val downloadState = state.downloadState
        val downloadButtonText = stringResource(
            if (savedFile != null) R.string.install else R.string.download
        ).plus(if (downloadState is Loading) ": ${downloadState.progress} %" else "")
        AppButton(
            text = downloadButtonText,
            enabled = downloadState !is Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            savedFile?.let {
                viewModel.installMod(context, it)
            } ?: run {
                viewModel.downloadFile()
                viewModel.showReview(activity)
            }
        }
        savedFile?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = buildAnnotatedString {
                    append(stringResource(R.string.the_file_is_saved_the_path))
                    withStyle(SpanStyle(color = Colors.BUTTON_RED)) {
                        append(it.path)
                    }
                },
                style = Typography.APP_TEXT,
                fontSize = 16.sp,
                color = Colors.GRAY,
                fontFamily = Fonts.INTER.MEDIUM,
            )
        }
    }
}