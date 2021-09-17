package de.uriegel.superfit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.activity.ComponentActivity
import androidx.preference.*
import de.uriegel.activityextensions.ActivityRequest
import de.uriegel.superfit.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PreferenceFragment(activity: ComponentActivity) : PreferenceFragmentCompat(), CoroutineScope {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        val bikeSupport = findPreference<CheckBoxPreference>(BIKE_SUPPORT)
        val editTextPreference = findPreference<EditTextPreference>(PREF_WHEEL)
        bikeSupport?.setOnPreferenceClickListener {
            editTextPreference?.isEnabled = bikeSupport.isChecked == true
            true
        }
        editTextPreference?.isEnabled = bikeSupport?.isChecked == true
        editTextPreference?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }

        val map = findPreference<Preference>(PREF_MAP)
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
                        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                        preferences.edit().putString(PREF_MAP, it.toString()).apply()
                        // TODO: show map in fragment
                    }
                }
            }
            true
        }
    }

    override val coroutineContext = Dispatchers.Main

    private val activityRequest: ActivityRequest = ActivityRequest(activity)


    companion object {
        const val PREF_MAP = "PREF_MAP"
        const val PREF_WHEEL = "PREF_WHEEL"
        const val BIKE_SUPPORT = "bike_support"
    }
}