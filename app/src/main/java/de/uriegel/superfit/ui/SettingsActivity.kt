package de.uriegel.superfit.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class SettingsActivity : AppCompatActivity() {

    class SettingsFragment() : PreferenceFragmentCompat(), CoroutineScope {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

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

//                launch {
//                    val result = activityRequest.launch(intent)
//                    if (result.resultCode == Activity.RESULT_OK) {
//                        result.data?.data?.also {
//                            val contentResolver = requireActivity().contentResolver
//                            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                            contentResolver.takePersistableUriPermission(it, takeFlags)
                            //preferences.edit().putString(PREF_MAP, it.toString()).apply()
                            //map.summary = it.path
//                        }
  //                  }
    //            }
                true
            }
        }
        override val coroutineContext = Dispatchers.Main

        companion object {
            const val PREF_MAP = "PREF_MAP"
            const val PREF_BIKE_SENSOR = "PREF_BIKE_SENSOR"
            const val PREF_HEARTRATE_SENSOR = "PREF_HEARTRATE_SENSOR"
            const val PREF_WHEEL = "PREF_WHEEL"
            const val BIKE_SUPPORT = "bike_support"
        }
    }
}