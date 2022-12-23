package se.miun.empe2105.dt031g.bathingsites

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//https://www.youtube.com/watch?v=-dm4cYlKiAA

class ShowSavedBathingSites : AppCompatActivity() {



    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_saved_bathing_sites)
        appDatabase = AppDatabase.getDatabase(this)


        val textview = findViewById<TextView>(R.id.will_be_recycler)

        readData(textview)
    }


    private fun readData(textView: TextView) {

        lateinit var bathingSites: List<SavedBathingSite>

        GlobalScope.launch {
            bathingSites = appDatabase.bathingSiteDao().getAllNames()
            displayData(bathingSites, textView)
        }
    }


    private suspend fun displayData(bathingSites: List<SavedBathingSite>, textView: TextView) {

        withContext(Dispatchers.Main) {
            //displaya men detta ska ju in i en recycler :)



            val stringBuilder = StringBuilder()


            for (i in bathingSites) {
                stringBuilder.append(i.name + " ")
            }

            val message = stringBuilder.toString()

            textView.text = message
        }
    }
}