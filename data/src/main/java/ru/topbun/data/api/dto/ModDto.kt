package ru.topbun.data.api.dto

import ru.topbun.domain.entity.mod.ModType

data class ModDto(
    val id: Int,
    val category: ModType,
    val rating: Double,
    val commentCounts: Int,
    val descriptionImages: List<String>,
    val title: String,
    val description: String,
    val image: String,
    val translations: List<ModTranslationDto>,
    val files: List<String>,
    val versions: List<VersionDto>,
)
