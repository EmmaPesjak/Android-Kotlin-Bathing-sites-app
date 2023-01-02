package se.miun.empe2105.dt031g.bathingsites

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//https://www.youtube.com/watch?v=Mc0XT58A1Z4
//https://www.youtube.com/watch?v=HtwDXRWjMcU
//https://www.youtube.com/watch?v=-dm4cYlKiAA

class ShowSavedBathingSites : AppCompatActivity() {



    private lateinit var appDatabase: AppDatabase

    var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_saved_bathing_sites)
        appDatabase = AppDatabase.getDatabase(this)

        readData()
    }


    private fun readData() {

        lateinit var bathingSites: List<SavedBathingSite>

        GlobalScope.launch {
            bathingSites = appDatabase.bathingSiteDao().getAllSites()
            displayData(bathingSites)
        }
    }


    private suspend fun displayData(bathingSites: List<SavedBathingSite>) {

        withContext(Dispatchers.Main) {


            val adapter = RecyclerAdapter(bathingSites) //radera att vi skickar in kontext?

            val recyclerView = findViewById<RecyclerView>(R.id.recycler)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(
                this@ShowSavedBathingSites, LinearLayoutManager.VERTICAL, false)



            adapter.onItemClick = {


                val dialogText = getString(R.string.name) + it.name + "\n" + getString(
                    R.string.description) + it.description + "\n" + getString(
                    R.string.address) + it.address + "\n" + getString(
                    R.string.longitude) + it.longitude + "\n" + getString(
                    R.string.latitude) + it.latitude + "\n" + getString(
                    R.string.grade) + it.grade + "\n" + getString(
                    R.string.water_temp) + it.waterTemp + "\n" + getString(
                    R.string.date_water) + it.dateTemp


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