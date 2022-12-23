package se.miun.empe2105.dt031g.bathingsites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowSavedBathingSites : AppCompatActivity() {



    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_saved_bathing_sites)
        appDatabase = AppDatabase.getDatabase(this)
    }


    private fun readData() {

        lateinit var bathingSites: List<SavedBathingSite>

        GlobalScope.launch {
            bathingSites = appDatabase.bathingSiteDao().getAllNames()
            displayData(bathingSites)
        }
    }


    private suspend fun displayData(bathingSites: List<SavedBathingSite>) {

        withContext(Dispatchers.Main) {
            //displaya
        }
    }
}