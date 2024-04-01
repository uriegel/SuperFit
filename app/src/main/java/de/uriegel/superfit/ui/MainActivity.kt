package de.uriegel.superfit.ui

import android.Manifest
import android.content.Context
import android.os.Build
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

        CoroutineScope(Dispatchers.Default).launch {
            showControls = dataStore.data.first()[prefBikeSupport] ?: false
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
                        startDestination = NavRoutes.CheckPermission.route
                    ) {
                        composable(NavRoutes.CheckPermission.route) {
                            val permissions = getPermissions()
                            PermissionCheck(permissions.map { it.permission }.toList().toTypedArray(),
                                    permissions.map { it.rationale }.toList().toTypedArray()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                    navController.navigate(NavRoutes.CheckBackgroundPermission.route) { popUpTo(0) }
                                else
                                    navController.navigate(NavRoutes.Main.route) { popUpTo(0) }
                            }
                        }
                        composable(NavRoutes.CheckBackgroundPermission.route) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                PermissionCheck(
                                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                    arrayOf(R.string.permission_location_rationale)
                                ) { navController.navigate(NavRoutes.Main.route) { popUpTo(0) } }
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
                        composable(NavRoutes.DevicesView.route + "/{titleId}/{uuid}",
                            arguments = listOf(
                                navArgument("titleId") { type = NavType.IntType},
                                navArgument("uuid") { type = NavType.StringType}
                            )) {
                                Devices(
                                    it.arguments?.getInt("titleId")!!,
                                    it.arguments?.getString("uuid")!!, {
                                        CoroutineScope(Dispatchers.Default).launch {
                                            dataStore.edit { pref ->
                                                pref[prefHeartSensor] = "${it.name}|${it.address}"
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
        val prefWheel = stringPreferencesKey("PREF_WHEEL")
        val prefBikeSupport = booleanPreferencesKey("bike_support")
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
