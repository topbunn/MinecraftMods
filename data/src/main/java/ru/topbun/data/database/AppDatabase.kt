package ru.topbun.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.topbun.data.database.dao.FavoriteDao
import ru.topbun.data.database.entity.FavoriteEntity

@Database(
    entities = [
        FavoriteEntity::class
    ], version = 1
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object{

        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "mcpe_mods.db"

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this){
            INSTANCE ?: run { buildInstance(context).also { INSTANCE = it } }
        }

        private fun buildInstance(context: Context) = Room.databaseBuilder(
            context, AppDatabase::class.java, DB_NAME).build()

    }

}