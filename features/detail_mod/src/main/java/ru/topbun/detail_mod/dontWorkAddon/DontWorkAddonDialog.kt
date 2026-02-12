package ru.topbun.detail_mod.dontWorkAddon

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import ru.topbun.android.ads.natives.NativeAdInitializer
import ru.topbun.ui.R
import ru.topbun.ui.components.CustomInputField
import ru.topbun.ui.components.DialogWrapper
import ru.topbun.ui.components.SendButton
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.utils.ObserveAsEvents

@Composable
fun Screen.DontWorkAddonDialog(
    onDismissDialog: () -> Unit
) {
    DialogWrapper(
        onDismissDialog = onDismissDialog,
        adContent = { NativeAdInitializer.show(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .border(2.dp, Colors.PRIMARY, RoundedCornerShape(22.dp))
        ) }
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            val context = LocalContext.current
            val viewModel = koinScreenModel<DontWorkAddonViewModel>()
            val state by viewModel.state.collectAsState()
            val messageSent = stringResource(R.string.message_is_sent)

            ObserveAsEvents(viewModel.events) {
                when(it){
                    is DontWorkAddonEvent.ShowError -> {
                        Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                    }
                    DontWorkAddonEvent.ShowSuccess -> {
                        Toast.makeText(context, messageSent, Toast.LENGTH_SHORT).show()
                        onDismissDialog()
                    }
                }
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.describe_your_problem),
                fontSize = 18.sp,
                fontFamily = Fonts.INTER.SEMI_BOLD,
                color = Colors.GRAY
            )
            Spacer(Modifier.height(16.dp))
            CustomInputField(
                modifier = Modifier.fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp),
                value = state.email,
                placeholder = stringResource(R.string.email),
                onValueChange = { viewModel.changeEmail(it) }
            )
            Spacer(Modifier.height(10.dp))
            CustomInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp),
                singleLine = false,
                value = state.message,
                placeholder = stringResource(R.string.type_message),
                onValueChange = { if (it.length < 1024) viewModel.changeMessage(it) }
            )
            Spacer(Modifier.height(16.dp))
            val buttonEnabled by viewModel.buttonEnabled.collectAsState()
            SendButton(
                isEnabled = buttonEnabled,
                isLoading = state.isLoading,
            ){
                viewModel.sendIssue()
            }
        }
    }
}