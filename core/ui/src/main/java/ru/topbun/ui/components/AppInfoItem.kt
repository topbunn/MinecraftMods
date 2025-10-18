package ru.topbun.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography

@Composable
fun AppInfoItem(imageLink: String, title: String, onClickItem: () -> Unit) {
    Column(
        Modifier.noRippleClickable(onClickItem)
    ){
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp)),
            model = imageLink,
            contentDescription = "image app",
            contentScale = ContentScale.FillWidth,
            onState = {
                if (it is AsyncImagePainter.State.Error){
                    Log.e("Error Async Image", it.result.throwable.message ?: "Неизвестно")
                }
            }
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            style = Typography.APP_TEXT,
            fontSize = 14.sp,
            color = Colors.WHITE,
            fontFamily = Fonts.SF.BOLD,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}