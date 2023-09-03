package de.uriegel.superfit.ui.views

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import de.uriegel.superfit.R
import de.uriegel.superfit.ui.NavRoutes

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionCheck(navController: NavController) {
    val context = LocalContext.current

    var permissionState by remember {
        mutableStateOf(false)
    }
    var backgroundLocationPermission by remember {
        mutableStateOf(false)
    }

    var textId by remember {
        mutableIntStateOf(R.string.permission_check)
    }

    val permissionStates = rememberMultiplePermissionsState(
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION)
    ) {
        permissionState = true
    }

    val backgroundLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION) {
        backgroundLocationPermission = true
    }

    val permission = if (permissionStates.permissions[1].status.isGranted)
        false
    else if (permissionStates.permissions[1].status.shouldShowRationale) {
        // TODO tell what permissions are mandatory
        textId = R.string.PERMISSION_SHOW_RATIONALE
        true
    }
    else
        false

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = textId),
            modifier = Modifier.padding(horizontal = 30.dp))
    }

//    val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
//        // If the user has denied the permission but the rationale can be shown,
//        // then gently explain why the app requires this permission
//        "The camera is important for this app. Please grant the permission."
//    } else {
//        // If it's the first time the user lands on this feature, or the user
//        // doesn't want to be asked again for this permission, explain that the
//        // permission is required
//        "Camera permission required for this feature to be available. " +
//                "Please grant the permission"
//    }
//    Text(textToShow)

    if (!permission) {
        SideEffect {
            if (permissionState)
                backgroundLocationPermissionState.launchPermissionRequest()
            else
                permissionStates.launchMultiplePermissionRequest()
        }

    }
    if (context.hasPermissions())
        navController.navigate(NavRoutes.Main.route) { popUpTo(0) }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Context.hasPermissions() =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        else
            true


