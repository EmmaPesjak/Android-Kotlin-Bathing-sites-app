package se.miun.empe2105.dt031g.bathingsites

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

/**
 * Activity class for showing the saved sites.
 * https://www.youtube.com/watch?v=Mc0XT58A1Z4
 * https://www.youtube.com/watch?v=HtwDXRWjMcU
 * https://www.youtube.com/watch?v=-dm4cYlKiAA
 */
class ShowBathingSitesActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase

    private var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_saved_bathing_sites)
        appDatabase = AppDatabase.getDatabase(this)
        readData()
    }

    /**
     * Method for reading data from the database.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun readData() {
        lateinit var bathingSites: List<BathingSite>

        // Reading can take time, do it in a coroutine.
        GlobalScope.launch {
            bathingSites = appDatabase.bathingSiteDao().getAllSites()
            displayData(bathingSites)
        }
    }

    /**
     * Method for displaying the bathing sites in a recycler view and creating alert dialogs
     * with the details.
     */
    private suspend fun displayData(bathingSites: List<BathingSite>) {

        withContext(Dispatchers.Main) {

            // Set up adapter and recycler view.
            val adapter = RecyclerAdapter(bathingSites)
            val recyclerView = findViewById<RecyclerView>(R.id.recycler)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this@ShowBathingSitesActivity,
                LinearLayoutManager.VERTICAL, false)

            // Create an alert dialog displaying bathing site details when clicking on a site.
            adapter.onItemClick = {

                // Create the dialog message.
                // https://stackoverflow.com/questions/36794883/whats-the-best-way-in-kotlin-for-an-null-objects-tostring-method-to-return-an
                val dialogText = getString(R.string.name) + it.name + getString(
                    R.string.description) + (it.description ?: "") +
                        getString(R.string.address) + (it.address ?: "") + getString(
                    R.string.longitude) + (it.longitude ?: "") +
                        getString(R.string.latitude) + (it.latitude ?: "") + getString(
                    R.string.grade) + (it.grade ?: "") + getString(R.string.water_temp) +
                        (it.waterTemp ?: "") + getString(R.string.date_water) + (it.dateTemp ?: "")

                val builder = AlertDialog.Builder(context)
                builder.setMessage(dialogText)
                builder.setNegativeButton(R.string.ok) { dialog, _ ->
                    dialog.cancel()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }
    }
}
