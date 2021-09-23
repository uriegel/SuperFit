package de.uriegel.superfit.sensor

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import de.uriegel.superfit.maps.LocationManager
import de.uriegel.superfit.sensor.BikeService.BIKE_CHARACTERISTICS_ID
import de.uriegel.superfit.ui.MainActivity
import de.uriegel.superfit.ui.PreferenceFragment
import kotlinx.coroutines.launch

import java.lang.Exception
import java.util.*
import kotlin.math.log

object BikeService : BluetoothLeService() {
    override fun getUuid() = uuid

    fun connect(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceAddress = preferences.getString(PreferenceFragment.PREF_BIKE_SENSOR, "") ?: ""
        connect(context, deviceAddress)
    }

    override fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService) {
        val bikeCharacteristics = service.characteristics?.find { it.uuid == UUID.fromString(BIKE_CHARACTERISTICS_ID) }
        bikeCharacteristics?.let {
            bluetoothGatt.setCharacteristicNotification(it, true)
            val descriptor = it.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTICS_ID))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt.writeDescriptor(descriptor)
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        //val flag = characteristic.properties
        val wheelCycles = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 1)
        val timestampWheel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 5)
        //val crankCycles = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 7)
        //val timestampCrank = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 9)
        if (lastWheelCycles != wheelCycles) {
            val timeSpan = if (timestampWheel > lastTimestampWheel)
                timestampWheel - lastTimestampWheel
            else
                timestampWheel + 0x10000 - lastTimestampWheel
            val cyclesPerSecs = (wheelCycles - lastWheelCycles).toFloat() / timeSpan.toFloat() * 1024
            val speed = 2.12 * cyclesPerSecs * 3.6
            launch {
                setSpeed?.invoke(speed)
            }

            lastWheelCycles = wheelCycles
            lastTimestampWheel = timestampWheel
        }
    }

    var setSpeed: ((speed: Double)->Unit)? = null
        set(value) { field = value }

//    private fun broadcastBikeUpdate(rate: Double) {
//        val intent = Intent(ACTION_GATT_DATA)
//        intent.putExtra(BIKE_RATE, rate)
//        sendBroadcast(intent)
//    }

    override fun getTag() = "BIKE"
    var lastWheelCycles = 0
    var lastTimestampWheel = 0

    private lateinit var deviceAddress: String
    private  const val uuid = "00001816-0000-1000-8000-00805f9b34fb"
    const val BIKE_CHARACTERISTICS_ID = "00002a5b-0000-1000-8000-00805f9b34fb"
    const val BIKE_RATE = "BIKE_RATE"
}