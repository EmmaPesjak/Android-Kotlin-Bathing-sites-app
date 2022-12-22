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

            val preferences = activity?.getSharedPreferences("fetch", Context.MODE_PRIVATE)
            val editor = preferences?.edit()

            // Set default php-site if there is no value.
            if (preferences?.getString("value", "").isNullOrBlank()) {
                editor?.putString("value", getString(R.string.default_address))
                editor?.apply()
            }

            // Set the summary text.
            val pref = findPreference("fetch") as EditTextPreference?
            pref?.summary = preferences?.getString("value", "")

            // Change summary text and value when the user changes the preference.
            pref?.setOnPreferenceChangeListener { _, newValue ->
                pref.summary = newValue.toString()

                editor?.clear()?.apply()
                editor?.putString("value", newValue.toString())?.apply()
                true
            }
        }
    }
}
