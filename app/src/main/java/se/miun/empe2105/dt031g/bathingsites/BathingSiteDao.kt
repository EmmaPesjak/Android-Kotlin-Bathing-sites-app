package se.miun.empe2105.dt031g.bathingsites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Bathing site data access object. Defines the query functions.
 */
@Dao
interface BathingSiteDao {

    /**
     * Get a list of all the bathing sites.
     */
    @Query("SELECT * FROM bathing_sites")
    fun getAllSites(): List<BathingSite>

    /**
     * Query to check if coordinates already exists in the database.
     * https://stackoverflow.com/questions/52739840/how-can-i-check-whether-data-exist-in-room-database-before-inserting-into-databa
     */
    @Query("SELECT EXISTS(SELECT * FROM bathing_sites WHERE longitude = :longitude AND latitude = :latitude)")
    fun coordsExists(longitude: Float?, latitude: Float?): Boolean

    /**
     * Get the amount of bathing sites.
     */
    @Query("SELECT COUNT(*) FROM bathing_sites")
    fun getAmount(): Int

    /**
     * Add a bathing site to the database.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bathingSite: BathingSite)
}