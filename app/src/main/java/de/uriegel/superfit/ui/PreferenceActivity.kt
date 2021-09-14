package de.uriegel.superfit.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import de.uriegel.superfit.R

class PreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, AppPreferenceFragment())
            .commit()

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val sdCard = getSdCard()
//        val mapsDir = "$sdCard/Maps"
//        val directory = File(mapsDir)
//        try {
//            val maps = directory.listFiles()?.filter { it.extension == "map" }?.map { it.name }
//            if (maps == null || maps.count() == 0)
//                throw Exception("Keine Einträge")
//        } catch (e: Exception) {
//            val builder = AlertDialog.Builder(this)
//
//            with(builder) {
//                setTitle("Kartenauswahl")
//                setMessage("Auf der SD-Karte im Verzeichnis '/Maps' müssen sich Karten befinden!")
//                show()
//            }
//        }
    }

    class AppPreferenceFragment : PreferenceFragmentCompat() {

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
                startActivityForResult(intent, 7)
                true
            }
//
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.data?.also {
            val contentResolver = applicationContext.contentResolver
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
            // Check for the freshest data.
            contentResolver.takePersistableUriPermission(it, takeFlags)
            val preferences = getSharedPreferences("default", MODE_PRIVATE)
            preferences.edit().putString("map", it.toString()).apply()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val MAP_DOWNLOAD = "MAP_DOWNLOAD"
        const val PREF_MAP = "PREF_MAP"
        const val PREF_WHEEL = "PREF_WHEEL"
        const val BIKE_SUPPORT = "bike_support"
    }
}