package de.uriegel.superfit.sensor

import android.bluetooth.*
import android.content.Context
import androidx.preference.PreferenceManager
import de.uriegel.superfit.android.logError
import de.uriegel.superfit.android.logInfo
import de.uriegel.superfit.android.logWarnung
import java.util.*

abstract class BluetoothLeService {

    open fun initialize(context: Context): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            logError("Unable to obtain a BluetoothAdapter.")
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
                logInfo(getLogId() + ": got device")
                bluetoothGatt = device.connectGatt(context, true, bluetoothGattCallback)
                true
            } catch (exception: IllegalArgumentException) {
                logWarnung("Device not found with provided address.")
                false
            }
        } ?: run {
            logWarnung("BluetoothAdapter not initialized")
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
                BluetoothProfile.STATE_CONNECTED -> {
                    logInfo(getLogId() + ": device connected")
                    bluetoothGatt?.discoverServices()
                }
                BluetoothProfile.STATE_CONNECTING -> logInfo(getLogId() + ": device connecting")
                BluetoothProfile.STATE_DISCONNECTING -> logInfo(getLogId() + ": device disconnecting")
                BluetoothProfile.STATE_DISCONNECTED -> logInfo(getLogId() + ": device disconnected")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGatt?.let { bluetoothGatt ->
                    logInfo(getLogId() + ": service discovered")
                    bluetoothGatt.services?.let { services ->
                        services.find { it.uuid == UUID.fromString(getUuid()) }
                            ?.let { service -> discoverService(bluetoothGatt, service) }
                    }
                }
            } else {
                logWarnung("onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            this@BluetoothLeService.onCharacteristicChanged(gatt, characteristic)
        }
    }

    abstract fun getUuid(): String
    protected abstract fun getLogId(): String
    protected abstract fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService)
    protected abstract fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic)
    protected abstract fun getPrefAddress(): String

    companion object {
        //const val BATTERY_CHARACTERISTICS_ID = "00002a38-0000-1000-8000-00805f9b34fb"
        const val CLIENT_CHARACTERISTICS_ID = "00002902-0000-1000-8000-00805f9b34fb"
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
}

