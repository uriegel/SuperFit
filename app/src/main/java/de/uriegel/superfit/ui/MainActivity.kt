package de.uriegel.superfit.ui

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import de.uriegel.superfit.R
import de.uriegel.superfit.ui.theme.SuperFitTheme
import de.uriegel.superfit.ui.views.Devices
import de.uriegel.superfit.ui.views.Display
import de.uriegel.superfit.ui.views.Main
import de.uriegel.superfit.ui.views.PermissionCheck
import de.uriegel.superfit.ui.views.Settings
import de.uriegel.superfit.ui.views.TrackMapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("RECOMPOSITION", "MainActivity onCreate") // Already good to have

        CoroutineScope(Dispatchers.Default).launch {
            showControls = dataStore.data.first()[prefBikeSupport] ?: false
        }

        setContent {
            Log.i("RECOMPOSITION", "setContent recomposing")
            SuperFitTheme {
                Log.i("RECOMPOSITION", "SuperFitTheme recomposing")
                val navController = rememberNavController()
                Log.i("RECOMPOSITION", "rememberNavController called, instance: ${navController.hashCode()}")
                var run by remember { mutableStateOf(false)}
                var run2 by remember { mutableStateOf(false)}

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.i("RECOMPOSITION", "Surface recomposing")
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.CheckPermission.route
                    ) {
                        Log.i("RECOMPOSITION", "NavHost recomposing/re-evaluating builder") // This will show when the NavHost's builder lambda is run
                        composable(NavRoutes.CheckPermission.route) {
                            if (!run) {
                                Log.i("TEST", "CheckPermission")
                                run = true

                                val permissions = getPermissions()
                                PermissionCheck(
                                    permissions.map { it.permission }.toList()
                                    .toTypedArray(),
                                    permissions.map { it.rationale }.toList().toTypedArray()
                                ) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                        navController.navigate(NavRoutes.CheckBackgroundPermission.route) {
                                            popUpTo(
                                                0
                                            )
                                        }
                                    else
                                        navController.navigate(NavRoutes.Main.route) { popUpTo(0) }
                                }
                            }
                        }
                        composable(NavRoutes.CheckBackgroundPermission.route) {
                            if (!run2) {
                                Log.i("TEST", "CheckBackgroundPermission")
                                run2 = true
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                    PermissionCheck(
                                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                        arrayOf(R.string.permission_location_rationale)
                                    ) { navController.navigate(NavRoutes.Main.route) { popUpTo(0) } }
                            }
                        }
                        composable(NavRoutes.Main.route) {
                            Log.i("TEST", "Main")
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

fun getPermissions() = sequence {
    yield(Permission(Manifest.permission.ACCESS_FINE_LOCATION,
        R.string.permission_location_rationale))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        yield(Permission(Manifest.permission.BLUETOOTH_SCAN,
            R.string.permission_blutooth_scan))
        yield(Permission(Manifest.permission.BLUETOOTH_CONNECT,
            R.string.permission_blutooth_connect))
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
        yield(Permission(Manifest.permission.READ_EXTERNAL_STORAGE,
            R.string.permission_external_storage_rationale))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        yield(Permission(Manifest.permission.POST_NOTIFICATIONS,
            R.string.permission_notification_rationale))
}

data class Permission(
    val permission: String,
    val rationale: Int
)
