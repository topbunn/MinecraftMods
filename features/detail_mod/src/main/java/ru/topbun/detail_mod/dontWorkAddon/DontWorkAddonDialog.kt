package ru.topbun.detail_mod.dontWorkAddon

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.topbun.detail_mod.dontWorkAddon.DontWorkAddonState.DontWorkScreenState
import ru.topbun.domain.entity.ConfigEntity
import ru.topbun.ui.R
import ru.topbun.ui.components.AppButton
import ru.topbun.ui.components.AppTextField
import ru.topbun.ui.components.DialogWrapper
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography

@Composable
fun DontWorkAddonDialog(
    config: ConfigEntity?,
    onDismissDialog: () -> Unit
) {
    DialogWrapper(config, onDismissDialog) {
        val context = LocalContext.current
        val viewModel = viewModel<DontWorkAddonViewModel>()
        val state by viewModel.state.collectAsState()
        val messageSent = stringResource(R.string.message_is_sent)

        LaunchedEffect(state.feedbackState) {
            when (val feedbackState = state.feedbackState) {
                is DontWorkScreenState.Error -> {
                    Toast.makeText(context, feedbackState.message, Toast.LENGTH_SHORT).show()
                }

                DontWorkScreenState.Success -> {
                    Toast.makeText(context, messageSent, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.describe_your_problem),
            style = Typography.APP_TEXT,
            fontSize = 16.sp,
            color = Colors.GRAY,
            fontFamily = Fonts.SF.MEDIUM,
        )
        Spacer(Modifier.height(16.dp))
        AppTextField(
            value = state.email,
            background = Colors.BLACK_BG,
            placeholder = stringResource(R.string.email),
            onValueChange = { viewModel.changeEmail(it) }
        )
        Spacer(Modifier.height(10.dp))
        AppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            alignment = Alignment.Top,
            singleLine = false,
            background = Colors.BLACK_BG,
            value = state.message,
            placeholder = stringResource(R.string.type_message),
            onValueChange = { if (it.length < 1024) viewModel.changeMessage(it) }
        )
        Spacer(Modifier.height(16.dp))
        val buttonEnabled by viewModel.buttonEnabled.collectAsState()
        AppButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = buttonEnabled,
            loading = state.feedbackState is DontWorkScreenState.Loading,
            text = stringResource(R.string.send)
        ) {
            viewModel.sendIssue()
        }
    }
}