package pion.tech.pionbase.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pion.tech.pionbase.data.model.dummy.DummyEntity
import pion.tech.pionbase.data.database.dao.DummyDAO
import pion.tech.pionbase.data.database.entity.FavoriteWallpaperEntity
import pion.tech.pionbase.data.database.dao.FavoriteDao

@Database(entities = [DummyEntity::class, FavoriteWallpaperEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDAO(): DummyDAO
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}
