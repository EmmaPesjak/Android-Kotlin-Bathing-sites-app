package se.miun.empe2105.dt031g.bathingsites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Singleton so only one database will be created https://www.youtube.com/watch?v=NS7yYdW3Lho
@Database(entities = [SavedBathingSite :: class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun bathingSiteDao(): BathingSiteDao

    companion object{

        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            val tempInstance = INSTANCE

            // Check if there already is a database
            if (tempInstance != null) {
                return tempInstance
            }

            // Else return a new instance. Put in synchronized block to ensure
            // only one instance is created even if the function is called from different
            // threads at the same time.
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}