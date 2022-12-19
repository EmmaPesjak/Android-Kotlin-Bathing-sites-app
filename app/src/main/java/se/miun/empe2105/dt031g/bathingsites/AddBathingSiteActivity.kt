package se.miun.empe2105.dt031g.bathingsites

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddBathingSiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bathing_site)


        //Behöver få in datumet automatiskt men krashar bara


    }

    // Inflate the overflow menu.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    // Start the settings activity when it is selected.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_settings -> {
                //startActivity(Intent(this, SettingsActivity::class.java))
                Toast.makeText(this, "Hej settings", Toast.LENGTH_SHORT).show()
            }
            R.id.add_clear -> {
                clearFields()
            }
            R.id.add_save -> {
                saveSite()
            }
            R.id.add_show_weather -> {
                Toast.makeText(this, "Hej väder", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.name).text.clear()
        findViewById<EditText>(R.id.description).text.clear()
        findViewById<EditText>(R.id.address).text.clear()
        findViewById<EditText>(R.id.longitude).text.clear()
        findViewById<EditText>(R.id.latitude).text.clear()
        findViewById<RatingBar>(R.id.rating_bar).rating = 0F
        findViewById<EditText>(R.id.water_temp).text.clear()
        findViewById<EditText>(R.id.date_water).text.clear()

        setDate()
    }

    private fun saveSite() {

        val name = findViewById<EditText>(R.id.name)
        val address = findViewById<EditText>(R.id.address)
        val longitude = findViewById<EditText>(R.id.longitude)
        val latitude = findViewById<EditText>(R.id.latitude)

        val alertDialog = createMessageAndGetDialog()

        // Check if the mandatory fields are filled in
        if (name.text.isNotEmpty() && address.text.isNotEmpty()) {
            longitude.error = null
            latitude.error = null
            alertDialog.show()
            return
        } else if (name.text.isNotEmpty() && latitude.text.isNotEmpty() &&
            longitude.text.isNotEmpty()) {
            address.error = null
            alertDialog.show()
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

    private fun createMessageAndGetDialog() : AlertDialog {
        val name = findViewById<EditText>(R.id.name)
        val address = findViewById<EditText>(R.id.address)
        val longitude = findViewById<EditText>(R.id.longitude)
        val latitude = findViewById<EditText>(R.id.latitude)

        val dialogText = getString(R.string.name) + name.text + "\n" + getString(
            R.string.description) + findViewById<EditText>(R.id.description).text + "\n" + getString(
            R.string.address) + address.text + "\n" + getString(
            R.string.longitude) + longitude.text + "\n" + getString(
            R.string.latitude) + latitude.text + "\n" + getString(
            R.string.grade) + findViewById<RatingBar>(R.id.rating_bar).rating + "\n" + getString(
            R.string.water_temp) + findViewById<EditText>(R.id.water_temp).text + "\n" + getString(
            R.string.date_water) + findViewById<EditText>(R.id.date_water).text

        val builder = AlertDialog.Builder(this)
        builder.setMessage(dialogText)
        builder.setNegativeButton(R.string.ok) { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)

        return alertDialog
    }

    private fun setDate() {

        //detta behöver ju snyggas upp
        // https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android
        // https://stackoverflow.com/questions/54840729/error29-34-type-mismatch-inferred-type-is-string-but-editable-was-expecte

        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  //notera att detta blir telefonens tidszon
        val formattedDate = df.format(c)
        val dateTemp = findViewById<EditText>(R.id.date_water)
        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
        dateTemp.text = formattedDate.toEditable()
    }
}