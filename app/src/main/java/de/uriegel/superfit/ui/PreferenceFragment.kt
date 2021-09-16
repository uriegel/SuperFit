package de.uriegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import de.uriegel.superfit.R

class PreferenceFragment : PreferenceFragmentCompat() {

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

        val mapUpload = findPreference<Preference>(MAP_DOWNLOAD)
        mapUpload?.setOnPreferenceClickListener {
// startActivity(Intent(context, MapDownloadActivity::class.java))
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                // putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            }
//
//            TODO: startActivityForResult(intent, 7)
            true
        }
//            val listPreference = findPreference<ListPreference>(PREF_MAP)
//                val sdCard: String = context.getSdCard()
//            val mapsDir = "$sdCard/Maps"
//            val directory = File(mapsDir)
//            val maps = directory.listFiles()
//                ?.filter { file -> file.extension == "map" }
//                ?.map { file -> file.name }
//            if (maps != null) {
//                listPreference?.entries = maps.toTypedArray()
//                listPreference?.entryValues = listPreference?.entries
//            }
    }

    companion object {
        const val MAP_DOWNLOAD = "MAP_DOWNLOAD"
        //const val PREF_MAP = "PREF_MAP"
        const val PREF_WHEEL = "PREF_WHEEL"
        const val BIKE_SUPPORT = "bike_support"
    }
}