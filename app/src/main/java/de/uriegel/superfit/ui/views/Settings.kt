package de.uriegel.superfit.ui.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.CheckBoxPref
import com.jamal.composeprefs3.ui.prefs.TextPref
import de.uriegel.superfit.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Settings(dataStore: DataStore<Preferences>) {

    val settings = stringResource(R.string.settings)

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_title)) })},
        content = {
            PrefsScreen(
                modifier = Modifier.padding(it),
                dataStore = dataStore) {

                prefsGroup(title = settings) {
                    prefsItem {
                        CheckBoxPref(
                            key = "bike_support",
                            title = stringResource(R.string.bike_support),
                            summary = stringResource(R.string.bike_support_description)) }
                    prefsItem {
                        EditTextPref(
                            title = stringResource(R.string.bike_circumference),
                            summary = stringResource(R.string.bike_circumference_description),
                            dialogMessage = stringResource(R.string.bike_circumference_description),
                            dialogTitle = stringResource(R.string.bike_circumference),
                            key = "PREF_WHEEL",
                            numbers = true
                        )
                    }
                    prefsItem {
                        TextPref(
                            title = stringResource(R.string.bike_sensor),
                            summary = "",
                        )
                    }
                    prefsItem {
                        TextPref(
                            title = stringResource(R.string.heartrate_sensor),
                            summary = "",
                        )
                    }
                }
                prefsGroup(title = "Navigation") {
                    prefsItem {
                        TextPref(
                            title = stringResource(R.string.maps),
                            summary = "",
                        )
                    }
                }
            }
        }
    )
}

