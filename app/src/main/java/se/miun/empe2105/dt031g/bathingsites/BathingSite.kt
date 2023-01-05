package se.miun.empe2105.dt031g.bathingsites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Schema of a bathing site.
 * https://developer.android.com/training/data-storage/room
 * https://www.youtube.com/watch?v=NS7yYdW3Lho
 */
@Entity(tableName = "bathing_sites")
data class BathingSite(
    @PrimaryKey(autoGenerate = true) val id:Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "longitude") var longitude: Float?,
    @ColumnInfo(name = "latitude") var latitude: Float?,
    @ColumnInfo(name = "grade") val grade: Float?,
    @ColumnInfo(name = "water_temp") val waterTemp: Float?,
    @ColumnInfo(name = "date_temp") val dateTemp: String?
)
