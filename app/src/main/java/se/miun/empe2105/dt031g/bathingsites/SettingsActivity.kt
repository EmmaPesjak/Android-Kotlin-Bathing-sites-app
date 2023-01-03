package se.miun.empe2105.dt031g.bathingsites

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

/**
 * Settings class.
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Settings fragment class used in the app for choosing php-site to get weather data from.
     */
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            // Fetch weather set-up.
            val weatherPreferences = activity?.getSharedPreferences("fetch", Context.MODE_PRIVATE)
            val editor = weatherPreferences?.edit()

            // Set the summary text.
            val pref = findPreference("fetch") as EditTextPreference?
            pref?.summary = weatherPreferences?.getString("value", "")

            // Change summary text and value when the user changes the preference.
            pref?.setOnPreferenceChangeListener { _, newValue ->
                pref.summary = newValue.toString()

                editor?.clear()?.apply()
                editor?.putString("value", newValue.toString())?.apply()
                true
            }


            // Download sites set-up.
            val downloadPreferences = activity?.getSharedPreferences("download", Context.MODE_PRIVATE)
            val dlEditor = downloadPreferences?.edit()

            // Set the summary text.
            val preference = findPreference("download") as EditTextPreference?
            preference?.summary = downloadPreferences?.getString("dlValue", "")

            // Change summary text and value when the user changes the preference.
            preference?.setOnPreferenceChangeListener { _, newValue ->
                preference.summary = newValue.toString()

                dlEditor?.clear()?.apply()
                dlEditor?.putString("dlValue", newValue.toString())?.apply()
                true
            }


            // Download sites set-up.
            val mapsPreferences = activity?.getSharedPreferences("maps", Context.MODE_PRIVATE)
            val mapsEditor = mapsPreferences?.edit()

            // Set the summary text.
            val mapsPref = findPreference("maps") as EditTextPreference?
            mapsPref?.summary = mapsPreferences?.getString("mapsValue", "")

            // Change summary text and value when the user changes the preference.
            mapsPref?.setOnPreferenceChangeListener { _, newValue ->
                mapsPref.summary = newValue.toString()

                mapsEditor?.clear()?.apply()
                mapsEditor?.putString("mapsValue", newValue.toString())?.apply()
                true
            }
        }
    }
}
