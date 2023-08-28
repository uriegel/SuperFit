package de.uriegel.superfit.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import de.uriegel.superfit.R
import de.uriegel.superfit.ui.theme.MapsTestTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MapsTestTheme {
                val navController = rememberNavController()

                var permissionState by remember {
                    mutableStateOf(false)
                }
                val storagePermissionState = rememberPermissionState(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                CheckPermissions(storagePermissionState,
                    {permissionState = it}, {hasAllFilesPermission()})

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.Maps.route
                    ) {
                        composable(NavRoutes.Maps.route) {
                            MapsView(navController)
                        }
                        composable(NavRoutes.Controls.route) {
                            Main(navController)
                        }
                        composable(NavRoutes.Dialog.route + "/{stringId}",
                            arguments = listOf(navArgument("stringId") { type = NavType.IntType })
                        ) {
                            DialogScreen(navController, it.arguments?.getInt("stringId")!!)
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (permissionState)
                            navController.navigate(NavRoutes.Controls.route){ popUpTo(0) }
                        else
                            navController.navigate(NavRoutes.Dialog.route + "/${R.string.PERMISSION_DENIED}"){ popUpTo(0) }
                    } else {
                        when {
                            storagePermissionState.status.isGranted ->
                                navController.navigate(NavRoutes.Maps.route)
                            storagePermissionState.status.shouldShowRationale ->
                                navController.navigate(NavRoutes.Dialog.route + "/${R.string.PERMISSION_SHOW_RATIONALE}"){ popUpTo(0) }
                            storagePermissionState.isPermanentlyDenied() ->
                                navController.navigate(NavRoutes.Dialog.route + "/${R.string.PERMISSION_DENIED}"){ popUpTo(0) }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasAllFilesPermission() =
        Environment.isExternalStorageManager()
}

