package ru.topbun.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConfigEntity(
    val isAdEnabled: Boolean,
    val applovinOpen: String?,
    val applovinInter: String?,
    val applovinNative: String?,
    val yandexOpen: String?,
    val yandexInter: String?,
    val yandexNative: String?,
) : Parcelable
