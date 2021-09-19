package de.uriegel.superfit.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.uriegel.superfit.R
import de.uriegel.superfit.databinding.ActivitySensorDevicesBinding
import de.uriegel.superfit.ui.adapters.SensorDevicesAdapter

class SensorDevicesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceUuid = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.getString(SERVICE_UUID)
        } else
            savedInstanceState.getSerializable(SERVICE_UUID) as String

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.devices.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.devices.addItemDecoration(itemDecoration)
        binding.devices.setHasFixedSize(true)
        binding.devices.adapter = devicesAdapter

        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid.fromString(serviceUuid))
            .build()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        bleScanner = bluetoothAdapter.bluetoothLeScanner
        handler.postDelayed({
            scanning = false
            bleScanner?.stopScan(scanCallback)
        }, SCAN_PERIOD)
        scanning = true
        bleScanner?.startScan(listOf(scanFilter), scanSettings, scanCallback)
    }

    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var bleScanner: BluetoothLeScanner? = null

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            devicesAdapter.addDevice(result.device)
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BLE", "onScanFailed: code $errorCode")
        }
    }

    companion object {
        const val SERVICE_UUID = "SERVICE_UUID"
        const val RESULT_DEVICE = "RESULT_DEVICE"
    }

    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD = 10000L
    private var devicesAdapter = SensorDevicesAdapter {
        val intent = Intent()
        intent.putExtra(RESULT_DEVICE, it.address)
        setResult(RESULT_OK, intent)
        finish()
    }

    private lateinit var binding: ActivitySensorDevicesBinding
}