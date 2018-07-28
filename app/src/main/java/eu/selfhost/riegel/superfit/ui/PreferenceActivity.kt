package eu.selfhost.riegel.superfit.ui

import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import eu.selfhost.riegel.superfit.R

class PreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, AppPreferenceFragment()).commit()

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class AppPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}