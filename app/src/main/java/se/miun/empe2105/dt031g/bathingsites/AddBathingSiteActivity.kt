package se.miun.empe2105.dt031g.bathingsites

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Activity class for adding a new site.
 */
class AddBathingSiteActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bathing_site)
        appDatabase = AppDatabase.getDatabase(this)
    }

    /**
     * Inflate the overflow menu.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    /**
     * Set option responses.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                }
            R.id.add_clear -> {
                clearFields()
            }
            R.id.add_save -> {
                saveSite()
            }
            R.id.add_show_weather -> {

                val address = findViewById<EditText>(R.id.address)
                val longitude = findViewById<EditText>(R.id.longitude)
                val latitude = findViewById<EditText>(R.id.latitude)

                val dialog = WeatherFragment()

                // Check if the user has inputted coordinates and/or address to search with.
                // Create a dialog and remove unnecessary errors if so.
                if (latitude.text.isNotEmpty() && longitude.text.isNotEmpty()) {
                    dialog.searchWeather(this, "",
                        Integer.parseInt(longitude.text.toString()),
                        Integer.parseInt(latitude.text.toString()))
                    address.error = null
                } else if (address.text.isNotEmpty()) {
                    dialog.searchWeather(this, address.text.toString())
                    longitude.error = null
                    latitude.error = null
                } else { // Else show errors.
                    address.error = getString(R.string.required_place)
                    longitude.error = getString(R.string.required_place)
                    latitude.error = getString(R.string.required_place)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Method for clearing the input fields.
     */
    private fun clearFields() {
        findViewById<EditText>(R.id.name).text.clear()
        findViewById<EditText>(R.id.description).text.clear()
        findViewById<EditText>(R.id.address).text.clear()
        findViewById<EditText>(R.id.longitude).text.clear()
        findViewById<EditText>(R.id.latitude).text.clear()
        findViewById<RatingBar>(R.id.rating_bar).rating = 0F
        findViewById<EditText>(R.id.water_temp).text.clear()

        // Reset the date.
        setDate()
    }

    /**
     * Method for saving the bathing site, checks mandatory inputs and
     * displays the site in an alert dialog.
     */
    private fun saveSite() {

        val name = findViewById<EditText>(R.id.name)
        val address = findViewById<EditText>(R.id.address)
        val longitude = findViewById<EditText>(R.id.longitude)
        val latitude = findViewById<EditText>(R.id.latitude)

        val alertDialog = createMessageAndGetDialog(name, address, longitude, latitude)

        // Check if the mandatory fields are filled in. Remove unnecessary errors if so.
        if (name.text.isNotEmpty() && address.text.isNotEmpty()) {
            longitude.error = null
            latitude.error = null
            //alertDialog.show()  //här ska det ju sparas till databasen och typ visas en toast istället
            writeData()
            return
        } else if (name.text.isNotEmpty() && latitude.text.isNotEmpty() &&
            longitude.text.isNotEmpty()) {
            address.error = null
            //alertDialog.show() //här ska det ju sparas till databasen och typ visas en toast istället
            writeData()
            return
        }

        // If mandatory fields are missing, set errors.
        if (name.text.isEmpty()) {
            name.error = getString(R.string.required_name)
        }
        if (address.text.isEmpty()) {
            address.error = getString(R.string.required_place)
        }
        if (longitude.text.isEmpty()) {
            longitude.error = getString(R.string.required_place)
        }
        if (latitude.text.isEmpty()) {
            latitude.error = getString(R.string.required_place)
        }
    }


    private suspend fun displayData(bathingSites: List<SavedBathingSite>) {

        withContext(Dispatchers.Main) {
            //displaya
        }
    }

    private fun writeData() {




        val name = findViewById<EditText>(R.id.name).text.toString()
        val description = findViewById<EditText>(R.id.description).text.toString()
        val address = findViewById<EditText>(R.id.address).text.toString()
        val longitude = findViewById<EditText>(R.id.longitude).text.toString().toFloatOrNull()
        val latitude = findViewById<EditText>(R.id.latitude).text.toString().toFloatOrNull()
        val grade = findViewById<RatingBar>(R.id.rating_bar).rating
        val waterTemp = findViewById<EditText>(R.id.water_temp).text.toString().toFloatOrNull()
        val dateWater = findViewById<EditText>(R.id.date_water).text.toString()

        // https://stackoverflow.com/questions/52739840/how-can-i-check-whether-data-exist-in-room-database-before-inserting-into-databa

        var coordsExists : Boolean

        val savedBathingSite = SavedBathingSite(
            null, name, description, address, longitude, latitude, grade, waterTemp, dateWater
        )

        GlobalScope.launch(Dispatchers.IO) {
            //kolla om coordinaterna är unika
            coordsExists = appDatabase.bathingSiteDao().coordsExists(longitude, latitude)

            // lägg bara til om koordinaterna är unika
            if(!coordsExists) {
                appDatabase.bathingSiteDao().insert(savedBathingSite)
                makeSaveToast(coordsExists)
                clearFields()

                // sen ska jag återgå till mainactivity här och öka räknaren

            } else {
                makeSaveToast(coordsExists)
            }

        }

    }

    private fun makeSaveToast(boolean: Boolean) {


        // kraschar om jag lägger till två sites, får inte köra flera loopers??

        Looper.prepare()
        if (!boolean) {
            Toast.makeText(this, "site added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "site not unique", Toast.LENGTH_SHORT).show()
        }

    }

    private fun readData() {

        lateinit var bathingSites: List<SavedBathingSite>

        GlobalScope.launch {
            bathingSites = appDatabase.bathingSiteDao().getAllNames()
            displayData(bathingSites)
        }
    }





    /**
     * Method for creating the alert dialog.
     */
    private fun createMessageAndGetDialog(name : EditText, address: EditText, longitude: EditText, latitude:EditText) : AlertDialog {

        // Create the text
        val dialogText = getString(R.string.name) + name.text + "\n" + getString(
            R.string.description) + findViewById<EditText>(R.id.description).text + "\n" + getString(
            R.string.address) + address.text + "\n" + getString(
            R.string.longitude) + longitude.text + "\n" + getString(
            R.string.latitude) + latitude.text + "\n" + getString(
            R.string.grade) + findViewById<RatingBar>(R.id.rating_bar).rating + "\n" + getString(
            R.string.water_temp) + findViewById<EditText>(R.id.water_temp).text + "\n" + getString(
            R.string.date_water) + findViewById<EditText>(R.id.date_water).text

        // Create the alert dialog.
        val builder = AlertDialog.Builder(this)
        builder.setMessage(dialogText)
        builder.setNegativeButton(R.string.ok) { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        return alertDialog
    }

    /**
     * Method for setting the default date to today's date.
     */
    private fun setDate() {
        // https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android
        // https://stackoverflow.com/questions/54840729/error29-34-type-mismatch-inferred-type-is-string-but-editable-was-expecte

        val date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  //notera att detta blir telefonens tidszon
        val formattedDate = dateFormat.format(date)
        val dateTemp = findViewById<EditText>(R.id.date_water)
        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
        dateTemp.text = formattedDate.toEditable()
    }
}
