package se.miun.empe2105.dt031g.bathingsites

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * Main activity for the application.
 */
class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set click listener on the floating action button.
        val fab = findViewById<View>(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, AddBathingSiteActivity::class.java))
        }

        // Set default php-site for fetching weather if there is no value.
        val weatherPreferences = getSharedPreferences("fetch", Context.MODE_PRIVATE)
        val editor = weatherPreferences?.edit()
        if (weatherPreferences?.getString("value", "").isNullOrBlank()) {
            editor?.putString("value", getString(R.string.default_address))
            editor?.apply()
        }

        // Set default site for downloading bathing sites if there is no value.
        val downloadPreferences = getSharedPreferences("download", Context.MODE_PRIVATE)
        val dlEditor = downloadPreferences?.edit()
        if (downloadPreferences?.getString("dlValue", "").isNullOrBlank()) {
            dlEditor?.putString("dlValue", getString(R.string.default_download_url))
            dlEditor?.apply()
        }

        // Set default maps radius.
        val mapsPreferences = getSharedPreferences("maps", Context.MODE_PRIVATE)
        val mapsEditor = mapsPreferences?.edit()
        if (mapsPreferences?.getString("mapsValue", "").isNullOrBlank()) {
            mapsEditor?.putString("mapsValue", getString(R.string.default_map_radius))
            mapsEditor?.apply()
        }
    }

    /**
     * Set a click listener on the BathingSiteView. (Got null when set in onCreate()).
     * https://stackoverflow.com/questions/3264610/findviewbyid-returns-null
     */
    override fun onStart() {
        super.onStart()
        val bathingView = findViewById<BathingSitesView>(R.id.bathing_site_view)
        bathingView.setOnClickListener {
            startActivity(Intent(this, ShowBathingSitesActivity::class.java))
        }
        // Make sure the count is updated (especially when a site is added
        // in AddBathingSiteActivity and the user is taken back to the MainActivity).
        bathingView.showCount()
    }

    /**
     * Inflate the overflow menu.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Set option responses.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.main_download -> {
                startActivity(Intent(this, DownloadActivity::class.java))
            }
            R.id.main_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
