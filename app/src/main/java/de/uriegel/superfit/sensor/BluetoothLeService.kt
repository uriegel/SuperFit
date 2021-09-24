package de.uriegel.superfit.sensor

import android.bluetooth.*
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import java.util.*

abstract class BluetoothLeService {

    open fun initialize(context: Context): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e(getTag(), "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    fun connect(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceAddress = preferences.getString(getPrefAddress(),"") ?: ""

        bluetoothAdapter?.let {
            try {
                val device = it.getRemoteDevice(deviceAddress)
                bluetoothGatt = device.connectGatt(context, true, bluetoothGattCallback)
                true
            } catch (exception: IllegalArgumentException) {
                Log.w(getTag(), "Device not found with provided address.")
                false
            }
        } ?: run {
            Log.w(getTag(), "BluetoothAdapter not initialized")
            false
        }
    }

    fun stop() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> bluetoothGatt?.discoverServices()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGatt?.let {
                        bluetoothGatt -> bluetoothGatt.services?.let { services ->
                        services.find { it.uuid == UUID.fromString(getUuid()) }
                            ?.let { service -> discoverService(bluetoothGatt, service) }
                    }
                }
            } else {
                Log.w(getTag(), "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            this@BluetoothLeService.onCharacteristicChanged(gatt, characteristic)
        }
    }

    abstract fun getUuid(): String
    protected abstract fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService)
    protected abstract fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic)
    protected abstract fun getTag(): String
    protected abstract fun getPrefAddress(): String

    companion object {
        //const val BATTERY_CHARACTERISTICS_ID = "00002a38-0000-1000-8000-00805f9b34fb"
        const val CLIENT_CHARACTERISTICS_ID = "00002902-0000-1000-8000-00805f9b34fb"
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
}

