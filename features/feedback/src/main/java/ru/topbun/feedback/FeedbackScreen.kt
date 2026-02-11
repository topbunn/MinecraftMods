package ru.topbun.feedback

import android.R
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ru.topbun.ui.components.CustomInputField
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.utils.ObserveAsEvents

object FeedbackScreen: Screen, Tab {

    override val options @Composable get() = TabOptions(
        index = 0u,
        title = stringResource(ru.topbun.ui.R.string.tabs_feedback),
        icon = painterResource(ru.topbun.ui.R.drawable.ic_tabs_feedback)
    )

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val viewModel = koinScreenModel<FeedbackViewModel>()
        val state by viewModel.state.collectAsState()

        ObserveAsEvents(viewModel.events) {
            val messageRes = when(it){
                FeedbackEvent.ShowSuccess -> ru.topbun.ui.R.string.message_is_sent
                FeedbackEvent.ShowError -> ru.topbun.ui.R.string.error_check_internet
            }
            Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
        }

        LaunchedEffect(Unit) {
            viewModel.handleChangeState()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.BLACK_BG)
                .padding(horizontal = 12.dp)
                .padding(top = 48.dp),
        ) {
            Header()
            Spacer(modifier = Modifier.height(40.dp))
            Fields(
                email = state.email,
                message = state.message,
                onEmailChange = viewModel::changeEmail,
                onMessageChange = viewModel::changeMessage
            )
            Spacer(modifier = Modifier.height(32.dp))
            SendButton(
                isEnabled = state.sendIsValid,
                isLoading = state.isLoading,
            ){
                viewModel.sendFeedback()
            }
        }
    }

    @Composable
    private fun SendButton(
        isEnabled: Boolean,
        isLoading: Boolean,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            enabled = isEnabled && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Colors.PRIMARY,
                disabledContainerColor = Colors.PRIMARY.copy(0.5f),
                contentColor = Colors.WHITE,
                disabledContentColor = Colors.WHITE.copy(0.5f),
            )
        ) {
            if (isLoading){
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Colors.WHITE, strokeWidth = 1.5.dp)
            } else {
                Text(
                    text = stringResource(ru.topbun.ui.R.string.send),
                    fontSize = 16.sp,
                    fontFamily = Fonts.INTER.MEDIUM,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    @Composable
    private fun Fields(
        email: String,
        message: String,
        onEmailChange: (String) -> Unit,
        onMessageChange: (String) -> Unit,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CustomInputField(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                value = email,
                onValueChange = onEmailChange,
                placeholder = stringResource(ru.topbun.ui.R.string.email),
            )
            CustomInputField(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                value = message,
                onValueChange = onMessageChange,
                placeholder = stringResource(ru.topbun.ui.R.string.type_message),
                singleLine = false,
            )
        }
    }

    @Composable
    private fun Header() {
        Column {
            Text(
                text = stringResource(ru.topbun.ui.R.string.need_help),
                fontFamily = Fonts.INTER.SEMI_BOLD,
                fontSize = 28.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(ru.topbun.ui.R.string.feedback_description),
                fontSize = 16.sp,
                fontFamily = Fonts.INTER.MEDIUM,
                color = Colors.GRAY
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .width(48.dp)
                    .background(
                        Colors.PRIMARY,
                        RoundedCornerShape(50)
                    )
            )
        }
    }




}