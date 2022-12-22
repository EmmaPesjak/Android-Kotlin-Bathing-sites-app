package se.miun.empe2105.dt031g.bathingsites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bathing_sites")
data class SavedBathingSite(
    @PrimaryKey(autoGenerate = true) val id:Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "longitude") val longitude: Int?, //float/double?
    @ColumnInfo(name = "latitude") val latitude: Int?,
    @ColumnInfo(name = "grade") val grade: Int?,
    @ColumnInfo(name = "water_temp") val waterTemp: String?,
    @ColumnInfo(name = "date_temp") val dateTemp: String?
)
