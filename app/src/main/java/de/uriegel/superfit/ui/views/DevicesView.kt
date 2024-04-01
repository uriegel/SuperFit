package de.uriegel.superfit.ui.views

import android.Manifest
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import de.uriegel.superfit.R
import de.uriegel.superfit.sensor.HeartRateSensor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesView(titleId: Int, uuid: String, lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current) {

    var bleScanner: BluetoothLeScanner? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            //devicesAdapter.addDevice(result.device)
        }

        override fun onScanFailed(errorCode: Int) {
            //logError("onScanFailed: code $errorCode")
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
            Toast.makeText(context, R.string.permission_external_storage_blutooth_scan,
                Toast.LENGTH_LONG).show()
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            bleScanner?.stopScan(scanCallback)
        }
    }

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

                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewDevicesView() {
    DevicesView(R.string.heartBeat, HeartRateSensor.getUuid())
}
