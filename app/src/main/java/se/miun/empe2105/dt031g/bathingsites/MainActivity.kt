package se.miun.empe2105.dt031g.bathingsites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import se.miun.empe2105.dt031g.bathingsites.BathingSitesView.Companion.increaseSites

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab = findViewById<View>(R.id.fab)
        fab.setOnClickListener { view ->
            startActivity(Intent(this, AddBathingSiteActivity::class.java))
        }

        // varför i hela friden ska denna vara klickbar och öka räknaren?
        val bathingView = findViewById<View>(R.id.fragmentContainerView)
        bathingView.setOnClickListener {

            val newNmb = increaseSites()

            val nmb = findViewById<TextView>(R.id.bathing_site_nmb)
            nmb.text = newNmb.toString()
        }


    }




    // Inflate the overflow menu.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Start the settings activity when it is selected.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_settings -> {
                //startActivity(Intent(this, SettingsActivity::class.java))
                Toast.makeText(this, "Hej settings", Toast.LENGTH_SHORT).show()
            }
//            R.id.dial_download -> {
//                startActivity(Intent(this, DownloadActivity::class.java))
//            }
        }
        return super.onOptionsItemSelected(item)
    }
}