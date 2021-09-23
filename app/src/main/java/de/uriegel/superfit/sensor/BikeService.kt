package de.uriegel.superfit.sensor

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import de.uriegel.superfit.ui.PreferenceFragment

import java.util.*

object BikeService : BluetoothLeService() {
    override fun getUuid() = uuid

    override fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService) {
        service.characteristics?.find { characteristic -> characteristic.uuid == UUID.fromString(characteristics_id) }?.let {
            bluetoothGatt.setCharacteristicNotification(it, true)
            val descriptor = it.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTICS_ID))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt.writeDescriptor(descriptor)
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
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
            setSpeed?.invoke(speed)
            lastWheelCycles = wheelCycles
            lastTimestampWheel = timestampWheel
        }
    }

    var setSpeed: ((speed: Double)->Unit)? = null

    override fun getTag() = "BIKE"
    override fun getPrefAddress() = PreferenceFragment.PREF_BIKE_SENSOR

    private var lastWheelCycles = 0
    private var lastTimestampWheel = 0

    private const val uuid = "00001816-0000-1000-8000-00805f9b34fb"
    private const val characteristics_id = "00002a5b-0000-1000-8000-00805f9b34fb"
}