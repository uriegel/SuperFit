package de.uriegel.superfit.ui.views

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import de.uriegel.superfit.R

@Composable
fun Devices(navController: NavHostController, titleId: Int, uuid: String,
            onChoose: (device: Device)->Unit,
            lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current) {

    var bleScanner: BluetoothLeScanner? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    var devices: Array<Device> by remember { mutableStateOf(emptyArray())}

    val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            devices =
                if (devices.any{ it.address == result.device.address })
                    devices
                else
                    devices.plus(Device(result.device.name, result.device.address))
        }

        override fun onScanFailed(errorCode: Int) {
            Toast.makeText(context, "onScanFailed: code $errorCode",
                Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(lifecycleOwner) {

        val scanFilter = ScanFilter
            .Builder()
            .setServiceUuid(ParcelUuid.fromString(uuid))
            .build()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
            == PackageManager.PERMISSION_GRANTED) {
            bleScanner = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
                .getAdapter()
                .bluetoothLeScanner
            bleScanner?.startScan(listOf(scanFilter), scanSettings, scanCallback)
        } else
            Toast.makeText(context, R.string.permission_blutooth_scan,
                Toast.LENGTH_LONG).show()
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            bleScanner?.stopScan(scanCallback)
        }
    }
    DevicesView(navController, titleId, devices, onChoose)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DevicesView(navController: NavHostController, titleId: Int, devices: Array<Device>,
                        onChoose: (device: Device)->Unit) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(titleId)) } ) },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                Column(Modifier.verticalScroll(scrollState)) {
                    devices.map {
                        Card(modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .clickable {
                                onChoose(it)
                                navController.navigateUp()
                            }) {
                            ConstraintLayout(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            ) {
                                val (name, address) = createRefs()
                                Text(text = it.name,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .constrainAs(name) {
                                            start.linkTo(parent.start)
                                            end.linkTo(parent.end)
                                        }
                                )
                                Text(text = it.address,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .constrainAs(address) {
                                            top.linkTo(name.bottom)
                                            start.linkTo(parent.start)
                                            end.linkTo(parent.end)
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

data class Device(
    val name: String,
    val address: String
)

@Preview
@Composable
fun PreviewDevicesView() {
    DevicesView(
        rememberNavController(), R.string.heartBeat, arrayOf(
        Device("VR 105", "00989898"),
        Device("VR 109", "999xxx5")
    ), {})
}
