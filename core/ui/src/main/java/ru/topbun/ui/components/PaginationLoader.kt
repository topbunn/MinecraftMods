package ru.topbun.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.topbun.ui.R
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography

@Composable
fun PaginationLoader(
    isEndList: Boolean,
    isLoading: Boolean,
    isError: Boolean,
    isEmpty: Boolean,
    key: Any,
    onClickRetryLoad: () -> Unit,
    onLoad: () -> Unit
) {
    when{
        isError -> {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.retry)
            ) { onClickRetryLoad() }
        }
        !isEndList && isLoading -> {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = Colors.WHITE,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        !isEndList && !isLoading -> {
            LaunchedEffect(key) {
                onLoad()
            }
        }
        !isLoading && !isError && isEmpty -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
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