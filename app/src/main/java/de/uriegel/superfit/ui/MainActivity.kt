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
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.uriegel.superfit.ui.theme.MapsTestTheme
import de.uriegel.superfit.ui.views.Controls
import de.uriegel.superfit.ui.views.DialogScreen
import de.uriegel.superfit.ui.views.Settings

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
                        startDestination = NavRoutes.Main.route
                    ) {
                        composable(NavRoutes.Main.route) {
                            Main(navController)
                        }
                        composable(NavRoutes.ShowSettings.route) {
                            //ShowSettings(navController)
                            Settings(LocalContext.current.dataStore)
                        }
                        composable(NavRoutes.Controls.route) {
                            Controls()
                        }
                        composable(NavRoutes.Dialog.route + "/{stringId}",
                            arguments = listOf(navArgument("stringId") { type = NavType.IntType })
                        ) {
                            DialogScreen(it.arguments?.getInt("stringId")!!)
                        }
                    }
                    navController.navigate(NavRoutes.Main.route){ popUpTo(0) }
                }
            }
        }
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
}

