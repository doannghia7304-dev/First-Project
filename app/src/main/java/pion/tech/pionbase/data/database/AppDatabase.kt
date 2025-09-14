package pion.tech.pionbase.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pion.tech.pionbase.data.model.dummy.DummyEntity
import pion.tech.pionbase.data.database.dao.DummyDAO

@Database(entities = [DummyEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDAO(): DummyDAO

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}
