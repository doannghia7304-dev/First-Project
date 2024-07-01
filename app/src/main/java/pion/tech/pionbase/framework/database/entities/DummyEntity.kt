package pion.tech.pionbase.framework.database.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import pion.tech.pionbase.framework.database.entities.DummyEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
@Parcelize
data class DummyEntity(
    @PrimaryKey
    @ColumnInfo(name = ID)
    val id: Long,

    @ColumnInfo(name = VALUE)
    val value: String
) : Parcelable {

    companion object {
        const val TABLE_NAME = "DummyEntity"
        const val ID = "ID"
        const val VALUE = "VALUE"
    }
}
