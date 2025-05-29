package de.uriegel.superfit.ui.views

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import de.uriegel.superfit.R
import de.uriegel.superfit.ui.NavRoutes

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionsCheck(navController: NavHostController) {
    val permissionState = rememberMultiplePermissionsState(getPermissions().toList(), {  })

    if (permissionState.permissions.all { it.status.isGranted }) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            backgroundPermissionCheck(navController)
        else
            navController.navigate(NavRoutes.Main.route) { popUpTo(0) }
    } else {
        Scaffold(
            topBar = { TopAppBar(title = { Text(stringResource(R.string.app_title)) }) },
            content = {
                ConstraintLayout(
                    modifier =
                        Modifier
                            .padding(it)
                            .fillMaxHeight()
                            .fillMaxWidth()
                ) {
                    val (columns, button) = createRefs()
                    Column(
                        modifier = Modifier
                            .constrainAs(columns) {
                                top.linkTo(parent.top)
                                bottom.linkTo(button.top)
                            }) {
                        permissionState.permissions.filter { !it.status.isGranted }.map {
                            Text(
                                modifier = Modifier
                                    .padding(20.dp),
                                text = stringResource(
                                    if (it.permission == Manifest.permission.ACCESS_FINE_LOCATION)
                                        R.string.permission_location_rationale
                                    else if (it.permission == Manifest.permission.BLUETOOTH_CONNECT)
                                        R.string.permission_blutooth_connect
                                    else if (it.permission == Manifest.permission.BLUETOOTH_SCAN)
                                        R.string.permission_blutooth_scan
                                    else if (it.permission == Manifest.permission.READ_EXTERNAL_STORAGE)
                                        R.string.permission_external_storage_rationale
                                    else
                                        R.string.permission_notification_rationale)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .constrainAs(button) {
                                top.linkTo(columns.bottom)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }) {
                            Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                                Text(text = stringResource(R.string.permission))
                            }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun backgroundPermissionCheck(navController: NavHostController) {

    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    if (permissionState.status.isGranted)
        navController.navigate(NavRoutes.Main.route) { popUpTo(0) }
    else {
        Scaffold(
            topBar = { TopAppBar(title = { Text(stringResource(R.string.app_title)) }) },
            content = {
                ConstraintLayout(
                    modifier =
                        Modifier
                            .padding(it)
                            .fillMaxHeight()
                            .fillMaxWidth()
                ) {
                    val (columns, button) = createRefs()
                    Column(
                        modifier = Modifier
                            .constrainAs(columns) {
                                top.linkTo(parent.top)
                                bottom.linkTo(button.top)
                            }) {
                            Text(
                                modifier = Modifier
                                    .padding(20.dp),
                                text = stringResource(R.string.permission_location_always_rationale)
                            )
                        }
                    Box(
                        modifier = Modifier
                            .constrainAs(button) {
                                top.linkTo(columns.bottom)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }) {
                        Button(onClick = { permissionState.launchPermissionRequest() }) {
                            Text(text = stringResource(R.string.permission))
                        }
                    }
                }
            }
        )
    }
}

fun getPermissions() = sequence {
    yield(Manifest.permission.ACCESS_FINE_LOCATION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        yield(Manifest.permission.BLUETOOTH_SCAN)
        yield(Manifest.permission.BLUETOOTH_CONNECT)
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
        yield(Manifest.permission.READ_EXTERNAL_STORAGE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        yield(Manifest.permission.POST_NOTIFICATIONS)
}

