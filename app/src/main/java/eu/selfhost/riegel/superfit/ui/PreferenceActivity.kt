package eu.selfhost.riegel.superfit.ui

import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import eu.selfhost.riegel.superfit.R
import eu.selfhost.riegel.superfit.utils.getSdCard
import java.io.File

class PreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, AppPreferenceFragment()).commit()

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sdCard: String = getSdCard()
        val mapsDir = "$sdCard/Maps"
        val directory = File(mapsDir)
        try {
            val maps = directory.listFiles().filter { it.extension == "map" }.map { it.name }
            if (maps.count() == 0)
                throw Exception("Keine Einträge")
        } catch (e: Exception) {
            val builder = AlertDialog.Builder(this)

            with(builder)
            {
                setTitle("Kartenauswahl")
                setMessage("Auf der SD-Karte im Verzeichnis '/Maps' müssen sich Karten befinden!")
                show()
            }
        }
    }

    class AppPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference)

            val listPreference = findPreference(PREF_MAP) as DynamicListPreference
            listPreference.setLoadingListener {
                val sdCard: String = it.context.getSdCard()
                val mapsDir = "$sdCard/Maps"
                val directory = File(mapsDir)
                val maps = directory.listFiles().filter { file -> file.extension == "map" }
                        .map { file -> file.name }
                it.entries = maps.toTypedArray()
                it.entryValues = it.entries
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