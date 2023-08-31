package de.uriegel.superfit.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import de.uriegel.activityextensions.ActivityRequest
import de.uriegel.superfit.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class SettingsFragment() : PreferenceFragmentCompat(), CoroutineScope {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preference, rootKey)

            activityRequest = ActivityRequest(requireContext() as AppCompatActivity)
            val map = findPreference<Preference>(PREF_MAP)
            val preferences = getDefaultSharedPreferences(requireContext())
            preferences.getString(PREF_MAP, null)?.let {
                val uri = Uri.parse(it)
                map?.summary = uri.path
            }
            map?.setOnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }

                launch {
                    val result = activityRequest.launch(intent)
                    if (result.resultCode == Activity.RESULT_OK) {
                        result.data?.data?.also {
                            val contentResolver = requireActivity().contentResolver
                            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            contentResolver.takePersistableUriPermission(it, takeFlags)
                            preferences.edit().putString(PREF_MAP, it.toString()).apply()
                            map.summary = it.path
                        }
                    }
                }
                true
            }
        }
        override val coroutineContext = Dispatchers.Main

        private lateinit var activityRequest: ActivityRequest

        companion object {
            const val PREF_MAP = "PREF_MAP"
            const val PREF_BIKE_SENSOR = "PREF_BIKE_SENSOR"
            const val PREF_HEARTRATE_SENSOR = "PREF_HEARTRATE_SENSOR"
            const val PREF_WHEEL = "PREF_WHEEL"
            const val BIKE_SUPPORT = "bike_support"
        }    }
}