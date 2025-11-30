package ru.topbun.data.mappers

import android.content.Context
import ru.topbun.data.api.dto.ModDto
import ru.topbun.data.database.AppDatabase
import ru.topbun.domain.entity.mod.ModEntity

class ModMapper(context: Context){

    private val dao = AppDatabase.getInstance(context).favoriteDao()

    suspend fun toEntity(dto: ModDto) = ModEntity(
        id = dto.id,
        category = dto.category,
        rating = dto.rating,
        commentCounts = dto.commentCounts,
        descriptionImages = dto.descriptionImages.map { it.mapImageLink() },
        title = dto.title,
        description = dto.description,
        image = dto.image.mapImageLink(),
        files = dto.files,
        versions = dto.versions.map { it.version },
        isFavorite = dao.getFavorite(dto.id)?.status ?: false
    )

    suspend fun toEntity(dtoList: List<ModDto>) = dtoList.map { toEntity(it) }

    private fun String.mapImageLink() = if (this.take(2) == "/u") ru.topbun.data.BuildConfig.BASE_URL + this else this

}
