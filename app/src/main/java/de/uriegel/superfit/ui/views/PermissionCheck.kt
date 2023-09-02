package de.uriegel.superfit.ui.views

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import de.uriegel.superfit.ui.NavRoutes

@Composable
fun PermissionCheck(navController: NavController) {
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val affe = it
        // TODO Check permissions
        // TODO Dialog when permissions not available
        navController.navigate(NavRoutes.Main.route){ popUpTo(0) }
    }
    if (context.hasPermissions())
        navController.navigate(NavRoutes.Main.route){ popUpTo(0) }
    else
        SideEffect {
            requestPermissionLauncher.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
}

// TODO old android additional Permissions
//fun Context.hasPermissions() =
//    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED

fun Context.hasPermissions() =
    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED

