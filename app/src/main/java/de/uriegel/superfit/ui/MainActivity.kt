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
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.uriegel.superfit.R
import de.uriegel.superfit.ui.theme.MapsTestTheme
import de.uriegel.superfit.ui.views.Display
import de.uriegel.superfit.ui.views.Main
import de.uriegel.superfit.ui.views.PermissionCheck
import de.uriegel.superfit.ui.views.Settings
import de.uriegel.superfit.ui.views.TrackMapView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MapsTestTheme {
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
                            PermissionCheck(
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                                    arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                else
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                                    arrayOf(
                                        R.string.permission_external_storage_rationale,
                                        R.string.permission_location_rationale
                                    )
                                else
                                    arrayOf(R.string.permission_location_rationale)
                            ) {
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
                            Settings(LocalContext.current.dataStore)
                        }
                        composable(NavRoutes.Controls.route) {
                            Display()
                        }
                        composable(NavRoutes.TrackMapView.route  + "/{trackId}",
                            arguments = listOf(navArgument("trackId") { type = NavType.IntType })) {
                            TrackMapView(it.arguments?.getInt("trackId")!!)
                        }
                    }
                }
            }
        }
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
}

