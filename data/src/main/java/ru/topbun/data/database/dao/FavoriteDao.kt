package ru.topbun.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.topbun.data.database.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites WHERE status = 1")
    suspend fun getFavorites(): List<FavoriteEntity>

    @Query("SELECT * FROM favorites WHERE modId=:modId LIMIT 1")
    suspend fun getFavorite(modId: Int): FavoriteEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

}