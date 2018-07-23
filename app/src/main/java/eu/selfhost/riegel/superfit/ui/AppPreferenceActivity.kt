package eu.selfhost.riegel.superfit.ui

import android.preference.PreferenceActivity
import android.os.Bundle
import android.preference.PreferenceFragment
import eu.selfhost.riegel.superfit.R

class AppPreferenceActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, AppPreferenceFragment()).commit()
    }

    class AppPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference_app)
        }
    }
}