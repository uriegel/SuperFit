package de.uriegel.superfit.ui.views

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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import de.uriegel.superfit.R

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionCheck(permissionIds: Array<String>, permissionRationaleIds: Array<Int>, onGranted: ()->Unit) {
    var permissionState by remember { mutableIntStateOf(0) }

    val permissionStates = rememberMultiplePermissionsState(permissionIds.asList()) {permissionState++}

//    val backgroundLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION) {
//        backgroundLocationPermission = true
//    }

    val permissionsState =
        if (permissionStates.permissions.all { it.status.isGranted })
            PermissionsState.Granted
        else if (permissionStates.permissions.all { it.status.shouldShowRationale })
            PermissionsState.ShowRationale
        else
            PermissionsState.Denied

    if (permissionsState == PermissionsState.Granted) {
        onGranted()
        return
    }

    if (permissionState == 0)
        SideEffect {
            permissionStates.launchMultiplePermissionRequest()
        }

    if (permissionState != -1) {
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
                    Column(modifier = Modifier
                        .constrainAs(columns){
                            top.linkTo(parent.top)
                            bottom.linkTo(button.top)
                        }) {
                        permissionRationaleIds.map {
                            Text(
                                modifier = Modifier
                                    .padding(20.dp),
                                text = stringResource(id = it)
                            )
                        }
                    }
                    Box(modifier = Modifier
                        .constrainAs(button) {
                            top.linkTo(columns.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }) {
                        if (permissionsState == PermissionsState.ShowRationale)
                            Button(onClick = { permissionStates.launchMultiplePermissionRequest() }) {
                                Text(text = stringResource(R.string.permission))
                            }
                        else
                            Text(modifier = Modifier
                                .padding(20.dp),
                                text = stringResource(R.string.permission_denied))
                    }
                }
            }
        )
    }
}

enum class PermissionsState {
    Granted, ShowRationale, Denied
}
