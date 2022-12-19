package se.miun.empe2105.dt031g.bathingsites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class AddBathingSiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bathing_site)
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
        Toast.makeText(this, "Hej clear", Toast.LENGTH_SHORT).show()
    }

    private fun saveSite() {
        Toast.makeText(this, "Hej save", Toast.LENGTH_SHORT).show()
    }
}