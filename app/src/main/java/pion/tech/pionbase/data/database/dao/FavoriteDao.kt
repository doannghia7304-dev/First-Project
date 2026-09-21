package pion.tech.pionbase.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pion.tech.pionbase.data.database.entity.FavoriteWallpaperEntity

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(wallpaper: FavoriteWallpaperEntity)

    @Delete
    suspend fun deleteFavorite(wallpaper: FavoriteWallpaperEntity)

    @Query("SELECT * FROM ${FavoriteWallpaperEntity.TABLE_NAME}")
    fun getAllFavorites(): Flow<List<FavoriteWallpaperEntity>>

    @Query("SELECT EXISTS(SELECT * FROM ${FavoriteWallpaperEntity.TABLE_NAME} WHERE id = :wallpaperId)")
    fun isFavorite(wallpaperId: String): Flow<Boolean>
}
