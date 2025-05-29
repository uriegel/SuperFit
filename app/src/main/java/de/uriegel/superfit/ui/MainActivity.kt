package de.uriegel.superfit.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.uriegel.superfit.ui.theme.SuperFitTheme
import de.uriegel.superfit.ui.views.Devices
import de.uriegel.superfit.ui.views.Display
import de.uriegel.superfit.ui.views.Main
import de.uriegel.superfit.ui.views.PermissionsCheck
import de.uriegel.superfit.ui.views.Settings
import de.uriegel.superfit.ui.views.TrackMapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.Default).launch {
            showControls = dataStore.data.first()[prefBikeSupport] == true
        }

        setContent {
            SuperFitTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.CheckPermissions.route
                    ) {
                        composable(NavRoutes.CheckPermissions.route) {
                            PermissionsCheck(navController)
                        }
                        composable(NavRoutes.Main.route) {
                            Main(navController)
                        }
                        composable(NavRoutes.ShowSettings.route) {
                            Settings(LocalContext.current.dataStore, navController)
                        }
                        composable(NavRoutes.Controls.route) {
                            Display(window, showControls)
                        }
                        composable(NavRoutes.TrackMapView.route  + "/{trackId}",
                            arguments = listOf(navArgument("trackId") { type = NavType.IntType })) {
                            TrackMapView(navController, it.arguments?.getInt("trackId")!!)
                        }
                        composable(NavRoutes.DevicesView.route + "/{prefKey}/{titleId}/{uuid}",
                            arguments = listOf(
                                navArgument("prefKey") { type = NavType.StringType},
                                navArgument("titleId") { type = NavType.IntType},
                                navArgument("uuid") { type = NavType.StringType}
                            )) {
                                Devices(
                                    navController, it.arguments?.getInt("titleId")!!,
                                    it.arguments?.getString("uuid")!!, { device ->
                                        CoroutineScope(Dispatchers.Default).launch {
                                            dataStore.edit { pref ->
                                                pref[stringPreferencesKey(it
                                                    .arguments
                                                    ?.getString("prefKey")!!)] =
                                                    "${device.name}|${device.address}"
                                            }
                                        }
                                    })
                            }
                    }
                }
            }
        }
    }

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        var showControls = false
        val prefMaps = stringPreferencesKey("PREF_MAP")
        val prefHeartBeat = booleanPreferencesKey("PREF_HEART_BEAT")
        val prefHeartSensor = stringPreferencesKey("PREF_HEART_SENSOR")
        val prefBikeSensor = stringPreferencesKey("PREF_BIKE_SENSOR")
        val prefWheel = stringPreferencesKey("PREF_WHEEL")
        val prefBikeSupport = booleanPreferencesKey("bike_support")
        val prefPartnerModeName = stringPreferencesKey("PREF_PARTNERMODE_NAME")
    }
}

