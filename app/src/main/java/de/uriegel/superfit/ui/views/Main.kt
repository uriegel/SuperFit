package de.uriegel.superfit.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import de.uriegel.superfit.R
import de.uriegel.superfit.extensions.startService
import de.uriegel.superfit.extensions.stopService
import de.uriegel.superfit.models.ServiceModel
import de.uriegel.superfit.ui.views.ServiceAlertDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Main(navController: NavHostController, viewModel: ServiceModel = viewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope =  rememberCoroutineScope()
    val context = LocalContext.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                val affe = it
            })

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog)
        ServiceAlertDialog(
            { showDialog = false },
            {
                viewModel.servicePending.value = true
                context.stopService()
            }
        )
    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                Divider()
                NavigationDrawerItem(
                    label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
            }
        }) {
        Scaffold(
            topBar = {
                TopBar(navController, onNavigationIconClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed)
                                open()
                            else
                                close()
                        }
                    }
                })
            },
            content = {
                ConstraintLayout(
                    modifier =
                    Modifier
                        .padding(it)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val (startBtn, stopBtn) = createRefs()
                    Button(
                        modifier = Modifier
                            .height(100.dp)
                            .constrainAs(startBtn) {
                                start.linkTo(parent.start, margin = 30.dp)
                                end.linkTo(parent.end, margin = 30.dp)
                                top.linkTo(parent.top)
                                bottom.linkTo(stopBtn.top)
                                width = Dimension.fillToConstraints
                            },
                        enabled = !viewModel.servicePending.value && !viewModel.serviceRunning.value,
                        onClick = {

                            if (context.hasPermissions()) {
                                // Permission already granted, update the location
                                viewModel.servicePending.value = true
                                context.startService()
                                navController.navigate(NavRoutes.Controls.route)
                            } else {
                                // Request location permission
                                requestPermissionLauncher.launch(arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                )
                            }
                        }) {
                        Text(text = stringResource(R.string.start))
                    }
                    Button(
                        modifier = Modifier
                            .height(100.dp)
                            .constrainAs(stopBtn) {
                                start.linkTo(parent.start, margin = 30.dp)
                                end.linkTo(parent.end, margin = 30.dp)
                                top.linkTo(startBtn.bottom)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                            },
                        enabled = !viewModel.servicePending.value && viewModel.serviceRunning.value,
                        onClick = { showDialog = true }) {
                        Text(text = stringResource(R.string.stop))
                    }
                }
            }
        )
    }
}


@Preview()
@Composable
fun Preview() {
    Main(rememberNavController())
}

//fun Context.hasPermissions() =
//    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED

fun Context.hasPermissions() =
    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED


