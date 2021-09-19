package de.uriegel.superfit.sensor

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

abstract class BluetoothLeService: CoroutineScope {

    override val coroutineContext = Dispatchers.Main

    fun initialize(): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e(getTag(), "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    protected fun connect(context: Context, deviceAddress: String) {
        this.context = context
        bluetoothAdapter?.let {
            try {
                val device = it.getRemoteDevice(deviceAddress)
                bluetoothGatt = device.connectGatt(context, false, bluetoothGattCallback)
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

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    connectionState = STATE_CONNECTED
                    //broadcastUpdate(ACTION_GATT_CONNECTED)
                    bluetoothGatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectionState = STATE_DISCONNECTED
                    //broadcastUpdate(ACTION_GATT_DISCONNECTED)
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGatt?.let {
                    it.services?.let { services ->
                        services.find { it.uuid == UUID.fromString(getUuid()) }
                            ?.let { service -> discoverService(it, service) }
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

    protected abstract fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService)
    protected abstract fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic)
    protected abstract fun getUuid(): String
    protected abstract fun getTag(): String

//    private fun broadcastUpdate(action: String) {
//        val intent = Intent(action)
//        sendBroadcast(intent)
//    }

    private fun close() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() : BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    companion object {
//        fun makeGattUpdateIntentFilter(): IntentFilter {
//            return IntentFilter().apply {
//                addAction(ACTION_GATT_CONNECTED)
//                addAction(ACTION_GATT_DISCONNECTED)
//                addAction(ACTION_GATT_DATA)
//            }
//        }

        const val BATTERY_CHARACTERISTICS_ID = "00002a38-0000-1000-8000-00805f9b34fb"
        const val CLIENT_CHARACTERISTICS_ID = "00002902-0000-1000-8000-00805f9b34fb"
        const val DEVICE_ADDRESS = "DEVICE_ADDRESS"
        const val ACTION_GATT_CONNECTED = "de.uriegel.superfit.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "de.uriegel.superfit.ACTION_GATT_DISCONNECTED"
//        const val ACTION_GATT_DATA = "de.uriegel.superfit.ACTION_DATA_AVAILABLE"
        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2
    }

    private val binder = LocalBinder()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED
    protected lateinit var context: Context
}

