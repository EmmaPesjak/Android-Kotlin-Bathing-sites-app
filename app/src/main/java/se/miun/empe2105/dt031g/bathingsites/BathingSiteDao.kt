package se.miun.empe2105.dt031g.bathingsites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BathingSiteDao {
    @Query("SELECT * FROM bathing_sites")
    fun getAllSites(): List<SavedBathingSite>

    // https://stackoverflow.com/questions/52739840/how-can-i-check-whether-data-exist-in-room-database-before-inserting-into-databa
    // Query för att kolla om lat + long finns
    @Query("SELECT EXISTS(SELECT * FROM bathing_sites WHERE longitude = :longitude AND latitude = :latitude)")
    fun coordsExists(longitude: Float?, latitude: Float?): Boolean

    // Räkna hur många bathing sites
    @Query("SELECT COUNT(*) FROM bathing_sites")
    fun getAmount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)  //måste nog checka här om samma coordinater
    suspend fun insert(bathingSite: SavedBathingSite) //could take time will call with coroutine
}