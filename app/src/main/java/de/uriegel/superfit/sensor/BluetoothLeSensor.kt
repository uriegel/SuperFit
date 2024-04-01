package de.uriegel.superfit.sensor

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.widget.Toast
import java.util.UUID

abstract class BluetoothLeSensor {
    open fun initialize(context:Context): Boolean {
        bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
            .getAdapter()
        return (bluetoothAdapter != null).also {
            if (!it)
                Toast.makeText(context, "Unable to obtain a BluetoothAdapter.",
                    Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(context: Context, deviceAddress: String) {
//        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val deviceAddress = preferences.getString(getPrefAddress(),"") ?: ""

        bluetoothAdapter?.let {
            try {
                val device = it.getRemoteDevice(deviceAddress)
                bluetoothGatt = device.connectGatt(context, true, bluetoothGattCallback)
                true
            } catch (exception: IllegalArgumentException) {
                Toast.makeText(context, "Device not found with provided address.",
                    Toast.LENGTH_LONG).show()
                false
            }
        } ?: run {
            Toast.makeText(context, "BluetoothAdapter not initialized",
                Toast.LENGTH_LONG).show()
            false
        }
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        bluetoothGatt?.let {
            it.close()
            bluetoothGatt = null
        }
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    // logInfo(getLogId() + ": device connected")
                    bluetoothGatt?.discoverServices()
                }
//                BluetoothProfile.STATE_CONNECTING -> logInfo(getLogId() + ": device connecting")
//                BluetoothProfile.STATE_DISCONNECTING -> logInfo(getLogId() + ": device disconnecting")
//                BluetoothProfile.STATE_DISCONNECTED -> logInfo(getLogId() + ": device disconnected")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGatt?.let {bluetoothGatt ->
                    // logInfo(getLogId() + ": service discovered")
                    bluetoothGatt.services?.let { services ->
                            services.find { it.uuid == UUID.fromString(getUuid()) }
                            ?.let { service -> discoverService(bluetoothGatt, service) }
                    }
                }
            }
//            else
//                Toast.makeText(context, "onServicesDiscovered received: $status\"",
//                    Toast.LENGTH_LONG).show()
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            this@BluetoothLeSensor.onCharacteristicChanged(gatt, characteristic)
        }
    }

    abstract fun getUuid(): String
    // protected abstract fun getLogId(): String
    protected abstract fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService)
    protected abstract fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic)

    companion object {
        //const val BATTERY_CHARACTERISTICS_ID = "00002a38-0000-1000-8000-00805f9b34fb"
        const val CLIENT_CHARACTERISTICS_ID = "00002902-0000-1000-8000-00805f9b34fb"
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
}
