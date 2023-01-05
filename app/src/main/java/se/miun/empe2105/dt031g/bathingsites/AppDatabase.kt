package se.miun.empe2105.dt031g.bathingsites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Singleton Room database for the bathing sites of the applications.
 * https://www.youtube.com/watch?v=NS7yYdW3Lho
 */
@Database(entities = [BathingSite :: class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun bathingSiteDao(): BathingSiteDao

    // Companion object to let classes get instances of the database.
    companion object{
        @Volatile
        private var INSTANCE : AppDatabase? = null

        /**
         * Method for getting the database.
         */
        fun getDatabase(context: Context): AppDatabase{
            val tempInstance = INSTANCE

            // Check if there already is a database
            if (tempInstance != null) {
                return tempInstance     // If so, return that database.
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
