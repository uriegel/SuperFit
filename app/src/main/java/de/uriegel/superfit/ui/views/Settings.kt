package de.uriegel.superfit.ui.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.jamal.composeprefs3.ui.LocalPrefsDataStore
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.CheckBoxPref
import com.jamal.composeprefs3.ui.prefs.TextPref
import de.uriegel.superfit.R
import de.uriegel.superfit.sensor.BikeSensor
import de.uriegel.superfit.sensor.HeartRateSensor
import de.uriegel.superfit.ui.EditTextPref
import de.uriegel.superfit.ui.MainActivity.Companion.prefBikeSensor
import de.uriegel.superfit.ui.MainActivity.Companion.prefBikeSupport
import de.uriegel.superfit.ui.MainActivity.Companion.prefHeartBeat
import de.uriegel.superfit.ui.MainActivity.Companion.prefHeartSensor
import de.uriegel.superfit.ui.MainActivity.Companion.prefMaps
import de.uriegel.superfit.ui.MainActivity.Companion.prefPartnerModeName
import de.uriegel.superfit.ui.MainActivity.Companion.prefWheel
import de.uriegel.superfit.ui.MainActivity.Companion.showControls
import de.uriegel.superfit.ui.NavRoutes
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Settings(dataStore: DataStore<Preferences>, navController: NavHostController) {

    val settings = stringResource(R.string.settings)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedMap by remember { mutableStateOf("") }
    var heartBeatEnabled by remember { mutableStateOf(false) }
    var heartBeatSensor: Device? by remember { mutableStateOf(null) }
    var bikeSensor: Device? by remember { mutableStateOf(null) }
    var bikeEnabled by remember { mutableStateOf(false) }
    val prefs by remember { dataStore.data }.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        prefs?.get(prefMaps)?.also {
            selectedMap = it
        }
        prefs?.get(prefBikeSupport)?.also {
            showControls = it
            bikeEnabled = it
        }
        prefs?.get(prefHeartBeat)?.also {
            heartBeatEnabled = it
        }
        prefs?.get(prefHeartSensor)?.also {
            it
                .split('|')
                .let {
                    Device(it.first(), it.last())
                }.also {
                    heartBeatSensor = it
                }
        }
        prefs?.get(prefBikeSensor)?.also {
            it
                .split('|')
                .let {
                    Device(it.first(), it.last())
                }.also {
                    bikeSensor = it
                }
        }
    }

    LaunchedEffect(dataStore.data) {
        dataStore.data.collectLatest { pref ->
            pref[prefMaps]?.also {
                selectedMap = it
            }
            pref[prefBikeSupport]?.also {
                showControls = it
                bikeEnabled = it
            }
            pref[prefHeartBeat]?.also {
                heartBeatEnabled = it
            }
            pref[prefHeartSensor]?.also {
                it
                    .split('|')
                    .let {
                        Device(it.first(), it.last())
                    }.also {
                        heartBeatSensor = it
                    }
            }
            pref[prefBikeSensor]?.also {
                it
                    .split('|')
                    .let {
                        Device(it.first(), it.last())
                    }.also {
                        bikeSensor = it
                    }
            }
        }
    }

    val mapsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.data?.also { uri ->
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                scope.launch {
                    dataStore.edit { preferences ->
                        preferences[prefMaps] = uri.toString()
                        selectedMap = uri.toString()
                        PreferenceManager
                            .getDefaultSharedPreferences(context)
                            .edit()
                            .putString("PREF_MAP", uri.toString())
                            .apply()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_title)) })},
        content = {
            PrefsScreen(
                modifier = Modifier.padding(it),
                dataStore = dataStore) {

                prefsGroup(title = settings) {
                    prefsItem {
                        CheckBoxPref(
                            key = prefHeartBeat.name,
                            title = stringResource(R.string.heartrate_sensor),
                            summary = stringResource(R.string.heartrate_description)) }
                    prefsItem {
                        TextPref(
                            title = stringResource(R.string.heartrate_sensor),
                            summary = heartBeatSensor?.name,
                            enabled = heartBeatEnabled,
                            darkenOnDisable = true,
                            onClick = {
                                navController.navigate(NavRoutes.DevicesView.route
                                        + "/${prefHeartSensor.name}/${R.string.heartrate_sensor}/${HeartRateSensor.getUuid()}")
                            }
                        )
                    }
                    prefsItem {
                        CheckBoxPref(
                            key = prefBikeSupport.name,
                            title = stringResource(R.string.bike_support),
                            summary = stringResource(R.string.bike_support_description)) }
                    prefsItem {
                        TextPref(
                            title = stringResource(R.string.bike_sensor),
                            enabled = bikeEnabled,
                            darkenOnDisable = true,
                            summary = bikeSensor?.name,
                            onClick = {
                                navController.navigate(NavRoutes.DevicesView.route
                                        + "/${prefBikeSensor.name}/${R.string.bike_sensor}/${BikeSensor.getUuid()}")
                            }
                        )
                    }
                    prefsItem {
                        EditTextPref(
                            title = stringResource(R.string.bike_circumference),
                            summary = stringResource(R.string.bike_circumference_description),
                            dialogMessage = stringResource(R.string.bike_circumference_description),
                            dialogTitle = stringResource(R.string.bike_circumference),
                            key = prefWheel,
                            enabled = bikeEnabled,
                            numbers = true
                        )
                    }
                }
                prefsGroup(title = "Navigation") {
                    prefsItem {
                        TextPref(
                            title = stringResource(R.string.maps),
                            summary = if (selectedMap.isNotEmpty()) selectedMap.getPath() else stringResource(R.string.maps_description),
                            enabled = true,
                            onClick = {
                                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = "*/*"
                                }
                                mapsLauncher.launch(intent)
                            })
                    }
                }
                prefsGroup(title = "Partnermodus") {
                    prefsItem {
                        EditTextPref(
                            title = "Dein Name",
                            summary = "Gib bitte deinen Namen ein",
                            dialogMessage = "Gib bitte deinen Namen ein",
                            dialogTitle = "Namen eingeben",
                            key = prefPartnerModeName
                        )
                    }
                }
            }
        }
    )
}

fun String.getPath() =
    Uri.parse(this).path

@Preview
@Composable
fun SettingsPreview() {
    Settings(LocalPrefsDataStore.current, rememberNavController())
}

