package se.miun.empe2105.dt031g.bathingsites

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RatingBar
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
     * Method for the save button in the overflow menu, checks mandatory inputs.
     */
    private fun saveSite() {

        val name = findViewById<EditText>(R.id.name)
        val address = findViewById<EditText>(R.id.address)
        val longitude = findViewById<EditText>(R.id.longitude)
        val latitude = findViewById<EditText>(R.id.latitude)

        // Check if the mandatory fields are filled in. Remove unnecessary errors if so.
        if (name.text.isNotEmpty() && address.text.isNotEmpty()) {
            longitude.error = null
            latitude.error = null
            writeData() // Save to db.
            return
        } else if (name.text.isNotEmpty() && latitude.text.isNotEmpty() &&
            longitude.text.isNotEmpty()) {
            address.error = null
            writeData() // Save to db.
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

    /**
     * Method for saving the bathing site in the database.
     */
    private fun writeData() {

        // Get all input.
        val name = findViewById<EditText>(R.id.name).text.toString()
        val description = findViewById<EditText>(R.id.description).text.toString()
        val address = findViewById<EditText>(R.id.address).text.toString()
        val longitude = findViewById<EditText>(R.id.longitude).text.toString().toFloatOrNull()
        val latitude = findViewById<EditText>(R.id.latitude).text.toString().toFloatOrNull()
        val grade = findViewById<RatingBar>(R.id.rating_bar).rating
        val waterTemp = findViewById<EditText>(R.id.water_temp).text.toString().toFloatOrNull()
        val dateWater = findViewById<EditText>(R.id.date_water).text.toString()

        // Create the site.
        val bathingSite = BathingSite(
            null, name, description, address, longitude, latitude, grade, waterTemp, dateWater
        )

        // https://stackoverflow.com/questions/52739840/how-can-i-check-whether-data-exist-in-room-database-before-inserting-into-databa
        var coordsExists : Boolean

        // Launch db queries in a coroutine.
        GlobalScope.launch(Dispatchers.IO) {
            // Check if the coordinates are unique. Null valued coordinates will still be added.
            coordsExists = appDatabase.bathingSiteDao().coordsExists(longitude, latitude)

            // Only add the site if the coordinates are unique.
            if(!coordsExists) {
                // Add to db.
                appDatabase.bathingSiteDao().insert(bathingSite)

                // Increase the count of bathing sites.
                BathingSitesView.count +=1

                // Dispatch an alert dialog with a success message in the main thread.
                withContext(Dispatchers.Main) {
                    android.app.AlertDialog.Builder(this@AddBathingSiteActivity)
                    .setMessage(getString(R.string.site_added))
                    .setNegativeButton(R.string.ok
                    ) { dialog, _ -> dialog.dismiss() }
                        .setOnDismissListener {
                            finish()
                        }
                    .show()
                }
            } else {
                // Else dispatch an alert dialog with a failed message in the main thread.
                withContext(Dispatchers.Main) {
                    android.app.AlertDialog.Builder(this@AddBathingSiteActivity)
                        .setMessage(getString(R.string.not_unique))
                        .setNegativeButton(R.string.ok
                        ) { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
        }
    }

    /**
     * Method for setting the default date to today's date.
     * https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android
     * https://stackoverflow.com/questions/54840729/error29-34-type-mismatch-inferred-type-is-string-but-editable-was-expecte
     */
    private fun setDate() {
        val date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        val dateTemp = findViewById<EditText>(R.id.date_water)
        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
        dateTemp.text = formattedDate.toEditable()
    }
}
