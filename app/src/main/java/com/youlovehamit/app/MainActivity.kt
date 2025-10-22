package com.youlovehamit.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ru.topbun.data.repository.ModRepository
import ru.topbun.domain.entity.ConfigEntity
import ru.topbun.ui.App
import ru.topbun.ui.components.OpenAppAd
import ru.topbun.ui.theme.Colors
import ru.topbun.ui.theme.colorScheme
import ru.topbun.ui.utils.requestPermissions

class MainActivity : ComponentActivity() {

    private val repository by lazy { ModRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            requestPermissions(Manifest.permission.POST_NOTIFICATIONS)

            var config by remember { mutableStateOf<ConfigEntity?>(null) }
            LaunchedEffect(Unit) { config = repository.getConfig() }


            config?.let { OpenAppAd(this, it.isAdEnabled, it.yandexOpen, it.applovinOpen) }
            MaterialTheme(colorScheme.copy(primary = Colors.PRIMARY)) {
                App()
            }
        }
    }



}