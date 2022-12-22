package se.miun.empe2105.dt031g.bathingsites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BathingSiteDao {
    @Query("SELECT name FROM bathing_sites")
    fun getAllNames(): List<SavedBathingSite> //stringar med namn?

    @Insert(onConflict = OnConflictStrategy.IGNORE)  //måste nog checka här om samma coordinater
    suspend fun insert(bathingSite: SavedBathingSite) //could take time will call with coroutine
}