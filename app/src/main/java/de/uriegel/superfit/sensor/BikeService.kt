package de.uriegel.superfit.sensor

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import de.uriegel.superfit.android.logInfo
import de.uriegel.superfit.android.logWarnung
import de.uriegel.superfit.ui.PreferenceFragment

import java.util.*

object BikeService : BluetoothLeService() {

    val cadence: MutableLiveData<Int> = MutableLiveData(-1)
    val velocityData: MutableLiveData<Float> = MutableLiveData(Float.NEGATIVE_INFINITY)
    val distanceData: MutableLiveData<Float> = MutableLiveData(Float.NEGATIVE_INFINITY)
    val durationData: MutableLiveData<Int> = MutableLiveData(-1)
    val averageVelocityData: MutableLiveData<Float> = MutableLiveData(Float.NEGATIVE_INFINITY)
    val maxVelocityData: MutableLiveData<Float> = MutableLiveData(Float.NEGATIVE_INFINITY)

    var distance = 0F
        private set
    var velocity = 0F
        private set
    var averageVelocity = 0F
        private set
    var duration = 0
        private set

    override fun initialize(context: Context, preferences: SharedPreferences?): Boolean {
        if (!super.initialize(context, preferences))
            return false

        preferences?.getString(PreferenceFragment.PREF_WHEEL, null)?.let {
            this.wheelCircumference = it.toInt()
        }

        stopwatch = StopWatch().also { sw ->
            sw.tick = {
                duration = it
                averageVelocity = (if (it > 0) distance / it else 0f) * 3600f
            }
        }

        lastWheelCycles = 0
        lastTimestampWheel = 0
        lastCrankCycles = 0
        lastTimestampCrank = 0
        maxVelocity = 0F
        distance = 0F
        duration = 0
        averageVelocity = 0F
        speedIsNull = true

        return this.wheelCircumference != 0
    }

    override fun getLogId() = "BIKE"
    override fun getUuid() = uuid

    override fun discoverService(bluetoothGatt: BluetoothGatt, service: BluetoothGattService) {
        service.characteristics?.find {
                characteristic -> characteristic.uuid == UUID.fromString(characteristics_id)
        }?.let {
            logInfo(getLogId() + ": characteristics found")
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

        velocity = wheelCircumference * cyclesPerSecs * 0.0036F
        val newDistance = wheelCircumference * wheelCycles / 1_000_000F
        if (newDistance > distance)
            distance = newDistance
        else
            logWarnung("Distance is false value: new distance: $newDistance, old distance: $distance")

        maxVelocity =
            if (velocity > maxVelocity)
                velocity
            else maxVelocity

        if (velocity > 4.0f && speedIsNull) {
            speedIsNull = false
            stopwatch?.start()
        } else if (velocity <= 4.0f && !speedIsNull) {
            speedIsNull = true
            stopwatch?.pause()
        }

        cadence.postValue(crankCyclesPerMin)
        velocityData.postValue(velocity)
        distanceData.postValue(distance)
        durationData.postValue(duration)
        averageVelocityData.postValue(averageVelocity)
        maxVelocityData.postValue(maxVelocity)
    }

    override fun getPrefAddress() = PreferenceFragment.PREF_BIKE_SENSOR

    private var lastWheelCycles = 0
    private var lastTimestampWheel = 0
    private var lastCrankCycles = 0
    private var lastTimestampCrank = 0
    private var wheelCircumference = 0
    private var maxVelocity = 0F
    private var speedIsNull = true

    private const val uuid = "00001816-0000-1000-8000-00805f9b34fb"
    private const val characteristics_id = "00002a5b-0000-1000-8000-00805f9b34fb"
    private var stopwatch: StopWatch? = null
}