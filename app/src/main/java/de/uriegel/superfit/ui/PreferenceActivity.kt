package de.uriegel.superfit.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import de.uriegel.superfit.R
import de.uriegel.superfit.utils.getSdCard
import java.io.File

class PreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, AppPreferenceFragment())
            .commit()

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sdCard = getSdCard()
        val mapsDir = "$sdCard/Maps"
        val directory = File(mapsDir)
        try {
            val maps = directory.listFiles()?.filter { it.extension == "map" }?.map { it.name }
            if (maps == null || maps.count() == 0)
                throw Exception("Keine Einträge")
        } catch (e: Exception) {
            val builder = AlertDialog.Builder(this)

            with(builder) {
                setTitle("Kartenauswahl")
                setMessage("Auf der SD-Karte im Verzeichnis '/Maps' müssen sich Karten befinden!")
                show()
            }
        }
    }

    class AppPreferenceFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preference, rootKey)

            val editTextPreference = findPreference<EditTextPreference>(PREF_WHEEL)
            editTextPreference?.setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val listPreference = findPreference<ListPreference>(PREF_MAP)
            val sdCard: String = context.getSdCard()
            val mapsDir = "$sdCard/Maps"
            val directory = File(mapsDir)
            val maps = directory.listFiles()
                ?.filter { file -> file.extension == "map" }
                ?.map { file -> file.name }
            if (maps != null) {
                listPreference?.entries = maps.toTypedArray()
                listPreference?.entryValues = listPreference?.entries
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val PREF_MAP = "PREF_MAP"
        const val PREF_WHEEL = "PREF_WHEEL"
    }
}