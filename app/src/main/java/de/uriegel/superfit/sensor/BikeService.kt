package de.uriegel.superfit.sensor

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import androidx.preference.PreferenceManager
import de.uriegel.superfit.model.BikeData
import de.uriegel.superfit.ui.PreferenceFragment

import java.util.*

object BikeService : BluetoothLeService() {

    override fun initialize(context: Context): Boolean {
        val result = super.initialize(context)
        if (result) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            preferences.getString(PreferenceFragment.PREF_WHEEL, null)?.let {
                this.wheelCircumference = it.toInt()
            }
        }

        lastWheelCycles = 0
        lastTimestampWheel = 0
        lastCrankCycles = 0
        lastTimestampCrank = 0
        maxVelocity = 0F

        return result && this.wheelCircumference != 0
    }

    override fun getUuid() = uuid

    override fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService) {
        service.characteristics?.find {
                characteristic -> characteristic.uuid == UUID.fromString(characteristics_id)
        }?.let {
            bluetoothGatt.setCharacteristicNotification(it, true)
            val descriptor = it.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTICS_ID))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt.writeDescriptor(descriptor)
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val wheelCycles = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 1)
        val timestampWheel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 5)
        val timeSpan = if (timestampWheel > lastTimestampWheel)
            timestampWheel - lastTimestampWheel
        else
            timestampWheel + 0x10000 - lastTimestampWheel
        val cyclesPerSecs = (wheelCycles - lastWheelCycles).toFloat() / timeSpan.toFloat() * 1024
        lastWheelCycles = wheelCycles
        lastTimestampWheel = timestampWheel

        val crankCycles = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 7)
        val timestampCrank = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 9)

        val timeSpanCrank = if (timestampCrank > lastTimestampCrank)
            timestampCrank - lastTimestampCrank
        else
            timestampCrank + 0x10000 - lastTimestampCrank
        val crankCyclesPerSecs = (crankCycles - lastCrankCycles).toFloat() / timeSpanCrank.toFloat() * 1024

        lastCrankCycles = crankCycles
        lastTimestampCrank = timestampCrank
        val crankCyclesPerMin = (crankCyclesPerSecs * 60F).toInt()

        val velocity = wheelCircumference * cyclesPerSecs * 0.0036F
        val distance = wheelCircumference * wheelCycles / 1_000_000F

        maxVelocity =
            if (velocity > maxVelocity)
                velocity
            else maxVelocity
        setBikeData?.invoke(BikeData(
            velocity,
            distance,
            maxVelocity,
            crankCyclesPerMin
        ))
    }

    var setBikeData: ((bikeData: BikeData)->Unit)? = null

    override fun getTag() = "BIKE"
    override fun getPrefAddress() = PreferenceFragment.PREF_BIKE_SENSOR

    private var lastWheelCycles = 0
    private var lastTimestampWheel = 0
    private var lastCrankCycles = 0
    private var lastTimestampCrank = 0
    private var wheelCircumference = 0
    private var maxVelocity = 0F

    private const val uuid = "00001816-0000-1000-8000-00805f9b34fb"
    private const val characteristics_id = "00002a5b-0000-1000-8000-00805f9b34fb"
}