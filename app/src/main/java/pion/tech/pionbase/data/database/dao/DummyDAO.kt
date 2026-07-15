package pion.tech.pionbase.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pion.tech.pionbase.data.model.dummy.DummyEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface DummyDAO {
    @Insert
    suspend fun insert(vararg scale: DummyEntity)

    @Update
    suspend fun update(vararg string: DummyEntity)

    @Delete
    suspend fun delete(scale: DummyEntity)

    @Query("SELECT * FROM ${DummyEntity.Companion.TABLE_NAME} ORDER BY ${DummyEntity.Companion.ID} DESC")
    fun getAllDummies(): Flow<List<DummyEntity>>
}
