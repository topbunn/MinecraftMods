package ru.topbun.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.util.DebugLogger
import ru.topbun.domain.entity.mod.ModEntity
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.Fonts
import ru.topbun.ui.theme.Typography
import ru.topbun.ui.R

@Composable
fun ModItem(mod: ModEntity, onClickFavorite: () -> Unit, onClickMod: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Colors.GRAY_BG)
            .clickable { onClickMod() }
            .padding(10.dp),
    ) {
        val context = LocalContext.current

        val request = remember(mod.image) {
            ImageRequest.Builder(context)
                .data(mod.image)
                .crossfade(false)
                .memoryCacheKey(mod.image)
                .diskCacheKey(mod.image)
                .build()
        }

        Box(contentAlignment = Alignment.Center){
            var isLoading by remember { mutableStateOf(true) }
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.7f)
                    .clip(RoundedCornerShape(6.dp)),
                model = request,
                contentDescription = "image mod",
                contentScale = ContentScale.Crop,
                onState = {
                    if (it !is AsyncImagePainter.State.Loading){
                        isLoading = false
                    }
                    if (it is AsyncImagePainter.State.Error){
                        Log.e("Error Async Image", it.result.throwable.message ?: "Неизвестно")
                    }
                }
            )
            if (isLoading){
                CircularProgressIndicator(
                    color = Colors.WHITE,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                modifier = Modifier.weight(1f),
                text = mod.title,
                style = Typography.APP_TEXT,
                fontSize = 18.sp,
                color = Colors.WHITE,
                fontFamily = Fonts.SF.BOLD,
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
            Image(
                modifier = Modifier.size(24.dp).rippleClickable{ onClickFavorite() },
                painter = painterResource(if (mod.isFavorite) R.drawable.ic_mine_heart_filled else R.drawable.ic_mine_heart_stroke),
                contentDescription = "status favorite mod",
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = mod.description,
            style = Typography.APP_TEXT,
            fontSize = 15.sp,
            color = Colors.GRAY,
            fontFamily = Fonts.SF.MEDIUM,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )

    }
}
